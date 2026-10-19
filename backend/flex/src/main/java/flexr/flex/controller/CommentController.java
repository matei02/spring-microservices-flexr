package flexr.flex.controller;

import flexr.flex.business.interfaces.CommentManager;
import flexr.flex.domain.dto.CommentPostRequest;
import flexr.flex.domain.dto.CommentResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Data
@RequestMapping("/comment")
public class CommentController {
    private final CommentManager commentManager;

    @PostMapping
    public CommentResponse createComment(@RequestBody CommentPostRequest request) {
        return commentManager.createComment(request);
    }

    @GetMapping("/{id}")
    public CommentResponse getCommentById(@PathVariable Long id) {
        return commentManager.getCommentById(id);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAllComments() {
        List<CommentResponse> comments = commentManager.getAllComments();
        if (comments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}")
    public CommentResponse updateComment(@PathVariable Long id, @RequestBody CommentPostRequest request) {
        return commentManager.updateComment(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentManager.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
