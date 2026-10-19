package flexr.flex.business.interfaces;

import flexr.flex.domain.dto.LikePostRequest;
import flexr.flex.domain.dto.LikeResponse;
import java.util.List;

public interface LikeManager {
    LikeResponse createLike(LikePostRequest request);
    LikeResponse getLikeById(Long id);
    List<LikeResponse> getAllLikes();
    void deleteLike(Long id);
} 