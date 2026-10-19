package flexr.flex.domain.classes;

import lombok.Data;
import java.time.Instant;

@Data
public class Post {
    private Long id;
    private String content;
    private String authorId;
    private byte[] image;
    private Instant createdAt;
}
