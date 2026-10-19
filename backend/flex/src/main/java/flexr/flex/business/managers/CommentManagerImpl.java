package flexr.flex.business.managers;

import flexr.flex.business.converters.CommentDtoConverter;
import flexr.flex.business.converters.CommentEntityConverter;
import flexr.flex.business.interfaces.CommentManager;
import flexr.flex.domain.classes.Comment;
import flexr.flex.domain.dto.CommentPostRequest;
import flexr.flex.domain.dto.CommentResponse;
import flexr.flex.persistence.entities.CommentEntity;
import flexr.flex.persistence.entities.PostEntity;
import flexr.flex.persistence.interfaces.CommentRepository;
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
public class CommentManagerImpl implements CommentManager {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public CommentResponse createComment(CommentPostRequest request) {
        Comment comment = CommentDtoConverter.toDomain(request);
        comment.setCreatedAt(Instant.now());
        PostEntity postEntity = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        CommentEntity entity = CommentEntityConverter.toEntity(comment, postEntity);
        CommentEntity saved = commentRepository.save(entity);
        return CommentDtoConverter.toResponse(CommentEntityConverter.toDomain(saved));
    }

    @Override
    public CommentResponse getCommentById(Long id) {
        CommentEntity entity = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        return CommentDtoConverter.toResponse(CommentEntityConverter.toDomain(entity));
    }

    @Override
    public List<CommentResponse> getAllComments() {
        return commentRepository.findAll().stream()
                .map(CommentEntityConverter::toDomain)
                .map(CommentDtoConverter::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse updateComment(Long id, CommentPostRequest request) {
        CommentEntity entity = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        entity.setText(request.getText());
        entity.setAuthorId(request.getAuthorId());
        CommentEntity updated = commentRepository.save(entity);
        return CommentDtoConverter.toResponse(CommentEntityConverter.toDomain(updated));
    }

    @Override
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }
        commentRepository.deleteById(id);
    }
}

