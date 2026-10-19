package flexr.flex.business.converters;

import flexr.flex.domain.classes.Like;
import flexr.flex.persistence.entities.LikeEntity;
import flexr.flex.persistence.entities.PostEntity;

public class LikeEntityConverter {
    public static Like toDomain(LikeEntity entity) {
        Like like = new Like();
        like.setId(entity.getId());
        like.setUserId(entity.getUserId());
        like.setPostId(entity.getPost() != null ? entity.getPost().getId() : null);
        like.setCreatedAt(entity.getCreatedAt());
        return like;
    }

    public static LikeEntity toEntity(Like like, PostEntity postEntity) {
        LikeEntity entity = new LikeEntity();
        entity.setId(like.getId());
        entity.setUserId(like.getUserId());
        entity.setCreatedAt(like.getCreatedAt());
        entity.setPost(postEntity);
        return entity;
    }
} 