package flexr.flex.persistence.interfaces;

import flexr.flex.persistence.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}

