package flexr.auth.business.managers;

import flexr.auth.business.converters.UserEntityConverter;
import flexr.auth.business.interfaces.UserManager;
import flexr.auth.domain.classes.User;
import flexr.auth.persistence.UserRepository;
import flexr.auth.persistence.entities.UserEntity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserManagerImpl implements UserManager {

    private final UserRepository userRepository;

    @Transactional
    public User getUserByUsername(String username) {
        return null;
    }

    @Transactional
    public List<User> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return userEntities.stream()
                .map(UserEntityConverter::convertToUser)
                .toList();
    }

    @Transactional
    public User getUserById(int id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isPresent()) {
            return UserEntityConverter.convertToUser(user.get());
        } else {
            throw new HttpServerErrorException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Transactional
    public void deleteUser(String username) {

    }

    @Transactional
    public boolean authenticateUser(String username, String password) {
        return false;
    }

    @Transactional
    public User getUserByAuth0Id(String auth0Id) {
        Optional<UserEntity> entity = userRepository.findByAuth0Id(auth0Id);
        if (entity.isEmpty()) {
            throw new HttpServerErrorException(HttpStatus.NOT_FOUND, "User not found");
        }
        return UserEntityConverter.convertToUser(entity.get());
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.findByAuth0Id(user.getAuth0Id()).isPresent()) {
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, "User already exists");
        }
        UserEntity entity = UserEntityConverter.convertToUserEntity(user);
        UserEntity saved = userRepository.save(entity);
        return UserEntityConverter.convertToUser(saved);
    }

    @Transactional
    public User updateUser(String auth0Id, String fullName, String handle) {
        Optional<UserEntity> entityOpt = userRepository.findByAuth0Id(auth0Id);
        if (entityOpt.isEmpty()) {
            throw new HttpServerErrorException(HttpStatus.NOT_FOUND, "User not found");
        }
        UserEntity entity = entityOpt.get();
        entity.setFullName(fullName);
        entity.setHandle(handle);
        UserEntity updated = userRepository.save(entity);
        return UserEntityConverter.convertToUser(updated);
    }

}
