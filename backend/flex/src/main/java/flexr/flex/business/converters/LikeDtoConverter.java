package flexr.flex.business.converters;

import flexr.flex.domain.classes.Like;
import flexr.flex.domain.dto.LikePostRequest;
import flexr.flex.domain.dto.LikeResponse;

public class LikeDtoConverter {
    public static Like toDomain(LikePostRequest request) {
        Like like = new Like();
        like.setUserId(request.getUserId());
        like.setPostId(request.getPostId());
        return like;
    }

    public static LikeResponse toResponse(Like like) {
        LikeResponse response = new LikeResponse();
        response.setId(like.getId());
        response.setUserId(like.getUserId());
        response.setPostId(like.getPostId());
        response.setCreatedAt(like.getCreatedAt());
        return response;
    }
} 