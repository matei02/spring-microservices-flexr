package flexr.flex.domain.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class PostResponse {
    private Long id;
    private String content;
    private String authorId;
    private byte[] image;
    private Instant createdAt;
} 