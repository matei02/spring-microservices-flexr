package flexr.auth.business.interfaces;

import flexr.auth.domain.classes.User;

import java.util.List;

public interface UserManager {
    // Define the methods that will be implemented by the UserManagerImpl class
    User createUser(User user);

    User getUserByUsername(String username);

    User getUserById(int id);

    void deleteUser(String username);

    boolean authenticateUser(String username, String password);

    User getUserByAuth0Id(String auth0Id);

    User updateUser(String auth0Id, String fullName, String handle);

    List<User> getAllUsers();

    // Add any other user-related methods as needed
}
