package flexr.auth.controller;

import flexr.auth.business.converters.DtoConverter;
import flexr.auth.business.interfaces.UserManager;
import flexr.auth.domain.classes.User;
import flexr.auth.domain.classes.UserRole;
import flexr.auth.domain.dto.CreateUserRequest;
import flexr.auth.domain.dto.UpdateUserRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Data
public class AuthController {

    private final UserManager userManager;

    @GetMapping("/users/auth0/{auth0Id}")
    public User getUserByAuth0Id(@PathVariable String auth0Id) {
        return userManager.getUserByAuth0Id(auth0Id);
    }

    @PostMapping("/users")
    public User createUser(@RequestBody CreateUserRequest request) {
        User user = DtoConverter.convertToUser(request);
        user.setRole(UserRole.USER);
        return userManager.createUser(user);
    }

    @PutMapping("/users/{auth0Id}")
    public User updateUser(@RequestBody UpdateUserRequest request, @PathVariable String auth0Id) {
        return userManager.updateUser(auth0Id, request.getFullName(), request.getHandle());
    }

    @GetMapping("/test")
    public String createUser() {
        return "This is a test endpoint to check if the server is running.";
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userManager.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }
}
