package flexr.flex.domain.classes;

import lombok.Data;
import java.time.Instant;

@Data
public class Like {
    private Long id;
    private String userId;
    private Long postId;
    private Instant createdAt;
}
