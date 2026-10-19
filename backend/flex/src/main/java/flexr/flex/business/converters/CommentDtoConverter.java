package flexr.flex.business.converters;

import flexr.flex.domain.classes.Comment;
import flexr.flex.domain.dto.CommentPostRequest;
import flexr.flex.domain.dto.CommentResponse;

public class CommentDtoConverter {
    public static Comment toDomain(CommentPostRequest request) {
        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setAuthorId(request.getAuthorId());
        comment.setPostId(request.getPostId());
        return comment;
    }

    public static CommentResponse toResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setText(comment.getText());
        response.setAuthorId(comment.getAuthorId());
        response.setPostId(comment.getPostId());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
} 