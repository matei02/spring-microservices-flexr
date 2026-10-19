package flexr.flex.persistence.interfaces;

import flexr.flex.persistence.entities.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
}
