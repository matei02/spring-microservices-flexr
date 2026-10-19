package flexr.flex.domain.dto;

import lombok.Data;

@Data
public class LikePostRequest {
    private String userId;
    private Long postId;
}
