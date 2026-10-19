package flexr.auth.business.converters;

import flexr.auth.domain.classes.User;
import flexr.auth.domain.classes.UserRole;
import flexr.auth.domain.dto.CreateUserRequest;
import flexr.auth.domain.dto.CreateUserResponse;
import flexr.auth.domain.dto.UpdateUserRequest;
import flexr.auth.persistence.entities.UserEntity;

public class DtoConverter {

    private DtoConverter() {
    }

    public static User convertToUser(CreateUserRequest request) {
        return User.builder()
                .handle(request.getHandle())
                .fullName(request.getFullName())
                .auth0Id(request.getAuth0Id())
                .email(request.getEmail())
                .build();
    }

    public static User convertToUser(UpdateUserRequest request) {
        return User.builder()
                .handle(request.getHandle())
                .fullName(request.getFullName())
                .build();
    }

    public static CreateUserResponse convertToUserDTO(User user) {
        return CreateUserResponse.builder()
                .auth0Id(user.getAuth0Id())
                .fullName(user.getFullName())
                .handle(user.getHandle())
                .email(user.getEmail())
                .build();
    }

}



