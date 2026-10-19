package flexr.flex.persistence.interfaces;

import flexr.flex.persistence.entities.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
}
