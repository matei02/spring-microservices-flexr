package flexr.flex.business.converters;

import flexr.flex.domain.classes.Post;
import flexr.flex.persistence.entities.PostEntity;

public class PostEntityConverter {
    public static Post toDomain(PostEntity entity) {
        Post post = new Post();
        post.setId(entity.getId());
        post.setContent(entity.getContent());
        post.setAuthorId(entity.getAuthorId());
        post.setImage(entity.getImage());
        post.setCreatedAt(entity.getCreatedAt());
        return post;
    }

    public static PostEntity toEntity(Post post) {
        PostEntity entity = new PostEntity();
        entity.setId(post.getId());
        entity.setContent(post.getContent());
        entity.setAuthorId(post.getAuthorId());
        entity.setImage(post.getImage());
        entity.setCreatedAt(post.getCreatedAt());
        return entity;
    }
} 