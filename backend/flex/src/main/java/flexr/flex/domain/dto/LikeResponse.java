package flexr.flex.domain.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class LikeResponse {
    private Long id;
    private String userId;
    private Long postId;
    private Instant createdAt;
} 