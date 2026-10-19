package flexr.flex.business.managers;

import flexr.flex.business.converters.LikeDtoConverter;
import flexr.flex.business.converters.LikeEntityConverter;
import flexr.flex.business.interfaces.LikeManager;
import flexr.flex.domain.classes.Like;
import flexr.flex.domain.dto.LikePostRequest;
import flexr.flex.domain.dto.LikeResponse;
import flexr.flex.persistence.entities.LikeEntity;
import flexr.flex.persistence.entities.PostEntity;
import flexr.flex.persistence.interfaces.LikeRepository;
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
public class LikeManagerImpl implements LikeManager {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Override
    public LikeResponse createLike(LikePostRequest request) {
        Like like = LikeDtoConverter.toDomain(request);
        like.setCreatedAt(Instant.now());
        PostEntity postEntity = postRepository.findById(like.getPostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        LikeEntity entity = LikeEntityConverter.toEntity(like, postEntity);
        LikeEntity saved = likeRepository.save(entity);
        return LikeDtoConverter.toResponse(LikeEntityConverter.toDomain(saved));
    }

    @Override
    public LikeResponse getLikeById(Long id) {
        LikeEntity entity = likeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Like not found"));
        return LikeDtoConverter.toResponse(LikeEntityConverter.toDomain(entity));
    }

    @Override
    public List<LikeResponse> getAllLikes() {
        return likeRepository.findAll().stream()
                .map(LikeEntityConverter::toDomain)
                .map(LikeDtoConverter::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteLike(Long id) {
        if (!likeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Like not found");
        }
        likeRepository.deleteById(id);
    }
} 