package flexr.auth.business.converters;

import flexr.auth.domain.classes.User;
import flexr.auth.domain.classes.UserRole;
import flexr.auth.persistence.entities.UserEntity;

public class UserEntityConverter {

    private UserEntityConverter() {
    }

    public static User convertToUser(UserEntity user) {
        return User.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .auth0Id(user.getAuth0Id())
                .handle(user.getHandle())
                .role(user.getRole().equals("ADMIN") ? UserRole.ADMIN : UserRole.USER)
                .email(user.getEmail())
                .build();
    }

    public static UserEntity convertToUserEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .auth0Id(user.getAuth0Id())
                .handle(user.getHandle())
                .role(user.getRole().toString())
                .email(user.getEmail())
                .build();
    }
}
