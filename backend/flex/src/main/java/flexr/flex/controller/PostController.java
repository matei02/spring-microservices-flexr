package flexr.flex.controller;

import flexr.flex.business.interfaces.PostManager;
import flexr.flex.domain.dto.CreatePostRequest;
import flexr.flex.domain.dto.PostResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Data
@RequestMapping("/post")
public class PostController {
    private final PostManager postManager;

    @PostMapping
    public PostResponse createPost(@RequestBody CreatePostRequest request) {
        return postManager.createPost(request);
    }

    @GetMapping("/{id}")
    public PostResponse getPostById(@PathVariable Long id) {
        return postManager.getPostById(id);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postManager.getAllPosts();
        if (posts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}")
    public PostResponse updatePost(@PathVariable Long id, @RequestBody CreatePostRequest request) {
        return postManager.updatePost(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postManager.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
