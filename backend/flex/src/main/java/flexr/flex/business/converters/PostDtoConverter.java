package flexr.flex.business.converters;

import flexr.flex.domain.classes.Post;
import flexr.flex.domain.dto.CreatePostRequest;
import flexr.flex.domain.dto.PostResponse;
import java.util.Base64;

public class PostDtoConverter {
    public static Post toDomain(CreatePostRequest request) {
        Post post = new Post();
        post.setContent(request.getContent());
        post.setAuthorId(request.getAuthorId());
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            post.setImage(Base64.getDecoder().decode(request.getImage()));
        } else {
            post.setImage(null);
        }
        return post;
    }

    public static PostResponse toResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setAuthorId(post.getAuthorId());
        response.setImage(post.getImage());
        response.setCreatedAt(post.getCreatedAt());
        return response;
    }
} 