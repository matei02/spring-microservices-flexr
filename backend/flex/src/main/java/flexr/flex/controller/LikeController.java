package flexr.flex.controller;

import flexr.flex.business.interfaces.LikeManager;
import flexr.flex.domain.dto.LikePostRequest;
import flexr.flex.domain.dto.LikeResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Data
@RequestMapping("/like")
public class LikeController {
    private final LikeManager likeManager;

    @PostMapping
    public LikeResponse createLike(@RequestBody LikePostRequest request) {
        return likeManager.createLike(request);
    }

    @GetMapping("/{id}")
    public LikeResponse getLikeById(@PathVariable Long id) {
        return likeManager.getLikeById(id);
    }

    @GetMapping
    public ResponseEntity<List<LikeResponse>> getAllLikes() {
        List<LikeResponse> likes = likeManager.getAllLikes();
        if (likes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(likes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLike(@PathVariable Long id) {
        likeManager.deleteLike(id);
        return ResponseEntity.noContent().build();
    }
}
