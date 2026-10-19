package flexr.flex.domain.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class CommentResponse {
    private Long id;
    private String text;
    private String authorId;
    private Long postId;
    private Instant createdAt;
} 