package flexr.flex.business.interfaces;

import flexr.flex.domain.dto.CommentPostRequest;
import flexr.flex.domain.dto.CommentResponse;
import java.util.List;

public interface CommentManager {
    CommentResponse createComment(CommentPostRequest request);
    CommentResponse getCommentById(Long id);
    List<CommentResponse> getAllComments();
    CommentResponse updateComment(Long id, CommentPostRequest request);
    void deleteComment(Long id);
}

