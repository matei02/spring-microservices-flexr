package flexr.auth.persistence;

import flexr.auth.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer>{
    boolean existsByFullName(String username);
    Optional<UserEntity> findByFullName(String username);
    Optional<UserEntity> findByAuth0Id(String auth0Id);
}
