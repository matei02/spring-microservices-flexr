package flexr.flex.domain.dto;

import lombok.Data;

@Data
public class CommentPostRequest {
    private String text;
    private String authorId;
    private Long postId;
}
