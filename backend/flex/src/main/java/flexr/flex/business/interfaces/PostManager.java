package flexr.flex.business.interfaces;

import flexr.flex.domain.dto.CreatePostRequest;
import flexr.flex.domain.dto.PostResponse;
import java.util.List;

public interface PostManager {
    PostResponse createPost(CreatePostRequest request);
    PostResponse getPostById(Long id);
    List<PostResponse> getAllPosts();
    PostResponse updatePost(Long id, CreatePostRequest request);
    void deletePost(Long id);
}

