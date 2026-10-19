package flexr.flex.domain.dto;

import lombok.Data;

@Data
public class CreatePostRequest {
    private String content;
    private String authorId;
    private String image;
}
