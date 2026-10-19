package flexr.flex.domain.classes;

import lombok.Data;
import java.time.Instant;

@Data
public class Comment {
    private Long id;
    private String text;
    private String authorId;
    private Long postId;
    private Instant createdAt;
}
