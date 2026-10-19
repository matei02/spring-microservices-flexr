package flexr.flex.business.managers;

import flexr.flex.business.converters.PostDtoConverter;
import flexr.flex.business.converters.PostEntityConverter;
import flexr.flex.business.interfaces.PostManager;
import flexr.flex.domain.classes.Post;
import flexr.flex.domain.dto.CreatePostRequest;
import flexr.flex.domain.dto.PostResponse;
import flexr.flex.persistence.entities.PostEntity;
import flexr.flex.persistence.interfaces.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostManagerImpl implements PostManager {
    private final PostRepository postRepository;

    @Override
    public PostResponse createPost(CreatePostRequest request) {
        Post post = PostDtoConverter.toDomain(request);
        post.setCreatedAt(Instant.now());
        PostEntity entity = PostEntityConverter.toEntity(post);
        PostEntity saved = postRepository.save(entity);
        return PostDtoConverter.toResponse(PostEntityConverter.toDomain(saved));
    }

    @Override
    public PostResponse getPostById(Long id) {
        PostEntity entity = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        return PostDtoConverter.toResponse(PostEntityConverter.toDomain(entity));
    }

    @Override
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(PostEntityConverter::toDomain)
                .map(PostDtoConverter::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponse updatePost(Long id, CreatePostRequest request) {
        PostEntity entity = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        entity.setContent(request.getContent());
        entity.setAuthorId(request.getAuthorId());
        byte[] imageBytes = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageBytes = java.util.Base64.getDecoder().decode(request.getImage());
        }
        entity.setImage(imageBytes);
        PostEntity updated = postRepository.save(entity);
        return PostDtoConverter.toResponse(PostEntityConverter.toDomain(updated));
    }

    @Override
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        postRepository.deleteById(id);
    }
}

