package flexr.flex.business.converters;

import flexr.flex.domain.classes.Comment;
import flexr.flex.persistence.entities.CommentEntity;
import flexr.flex.persistence.entities.PostEntity;

public class CommentEntityConverter {
    public static Comment toDomain(CommentEntity entity) {
        Comment comment = new Comment();
        comment.setId(entity.getId());
        comment.setText(entity.getText());
        comment.setAuthorId(entity.getAuthorId());
        comment.setPostId(entity.getPost() != null ? entity.getPost().getId() : null);
        comment.setCreatedAt(entity.getCreatedAt());
        return comment;
    }

    public static CommentEntity toEntity(Comment comment, PostEntity postEntity) {
        CommentEntity entity = new CommentEntity();
        entity.setId(comment.getId());
        entity.setText(comment.getText());
        entity.setAuthorId(comment.getAuthorId());
        entity.setCreatedAt(comment.getCreatedAt());
        entity.setPost(postEntity);
        return entity;
    }
} 