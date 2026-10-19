package flexr.auth.business.managers;

import flexr.auth.business.converters.UserEntityConverter;
import flexr.auth.business.interfaces.UserManager;
import flexr.auth.domain.classes.User;
import flexr.auth.domain.classes.UserRole;
import flexr.auth.persistence.UserRepository;
import flexr.auth.persistence.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagerImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserManagerImpl userManager;

    private UserEntity testUserEntity;
    private User testUserDomain;

    @BeforeEach
    void setUp() {
        testUserEntity = new UserEntity();
        testUserEntity.setId(1L);
        testUserEntity.setAuth0Id("auth0|123");
        testUserEntity.setFullName("Test User");
        testUserEntity.setHandle("test_handle");
        testUserEntity.setEmail("testuser@example.com");
        testUserEntity.setRole("USER");

        testUserDomain = User.builder()
                .id(1L)
                .auth0Id("auth0|123")
                .fullName("Test User")
                .handle("test_handle")
                .email("testuser@example.com")
                .role(UserRole.USER)
                .build();
    }

    @Test
    void getUserByUsername_shouldReturnNull() {
        // Given - current implementation returns null
        String username = "someuser";

        // When
        User result = userManager.getUserByUsername(username);

        // Then
        assertThat(result).isNull();
        verifyNoInteractions(userRepository);
    }

    @Test
    void getAllUsers_shouldReturnEmptyList_whenNoUsersExist() {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<User> result = userManager.getAllUsers();

        // Then
        assertThat(result).isNotNull().isEmpty();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers_whenUsersExist() {
        // Given
        List<UserEntity> userEntities = Arrays.asList(testUserEntity, new UserEntity());
        when(userRepository.findAll()).thenReturn(userEntities);

        try (MockedStatic<UserEntityConverter> mockedConverter = Mockito.mockStatic(UserEntityConverter.class)) {
            mockedConverter.when(() -> UserEntityConverter.convertToUser(any(UserEntity.class)))
                    .thenReturn(testUserDomain);

            // When
            List<User> result = userManager.getAllUsers();

            // Then
            assertThat(result).isNotNull().hasSize(2);
            assertThat(result.get(0)).isEqualTo(testUserDomain);
            verify(userRepository, times(1)).findAll();
            mockedConverter.verify(() -> UserEntityConverter.convertToUser(testUserEntity), times(1));
            mockedConverter.verify(() -> UserEntityConverter.convertToUser(any(UserEntity.class)), times(2));
        }
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {
        // Given
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUserEntity));

        try (MockedStatic<UserEntityConverter> mockedConverter = Mockito.mockStatic(UserEntityConverter.class)) {
            mockedConverter.when(() -> UserEntityConverter.convertToUser(any(UserEntity.class)))
                    .thenReturn(testUserDomain);

            // When
            User result = userManager.getUserById(userId);

            // Then
            assertThat(result).isEqualTo(testUserDomain);
            verify(userRepository, times(1)).findById(userId);
            mockedConverter.verify(() -> UserEntityConverter.convertToUser(testUserEntity), times(1));
        }
    }

    @Test
    void getUserById_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Given
        int userId = 99;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        HttpServerErrorException exception = assertThrows(HttpServerErrorException.class, () ->
                userManager.getUserById(userId)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getStatusText()).isEqualTo("User not found");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void deleteUser_shouldDoNothing_whenCalled() {
        // Given - current implementation is empty

        // When
        userManager.deleteUser("anyUser");

        // Then
        verifyNoInteractions(userRepository);
    }

    @Test
    void authenticateUser_shouldReturnFalse_whenCalled() {
        // Given - current implementation returns false
        String username = "user";
        String password = "pass";

        // When
        boolean result = userManager.authenticateUser(username, password);

        // Then
        assertThat(result).isFalse();
        verifyNoInteractions(userRepository);
    }

    @Test
    void getUserByAuth0Id_shouldReturnUser_whenUserExists() {
        // Given
        String auth0Id = "auth0|123";
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(testUserEntity));

        try (MockedStatic<UserEntityConverter> mockedConverter = Mockito.mockStatic(UserEntityConverter.class)) {
            mockedConverter.when(() -> UserEntityConverter.convertToUser(any(UserEntity.class)))
                    .thenReturn(testUserDomain);

            // When
            User result = userManager.getUserByAuth0Id(auth0Id);

            // Then
            assertThat(result).isEqualTo(testUserDomain);
            verify(userRepository, times(1)).findByAuth0Id(auth0Id);
            mockedConverter.verify(() -> UserEntityConverter.convertToUser(testUserEntity), times(1));
        }
    }

    @Test
    void getUserByAuth0Id_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Given
        String auth0Id = "nonexistent|id";
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.empty());

        // When / Then
        HttpServerErrorException exception = assertThrows(HttpServerErrorException.class, () ->
                userManager.getUserByAuth0Id(auth0Id)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getStatusText()).isEqualTo("User not found");
        verify(userRepository, times(1)).findByAuth0Id(auth0Id);
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenUserAlreadyExists() {
        // Given
        when(userRepository.findByAuth0Id(testUserDomain.getAuth0Id())).thenReturn(Optional.of(testUserEntity));

        // When / Then
        HttpServerErrorException exception = assertThrows(HttpServerErrorException.class, () ->
                userManager.createUser(testUserDomain)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getStatusText()).isEqualTo("User already exists");
        verify(userRepository, times(1)).findByAuth0Id(testUserDomain.getAuth0Id());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void createUser_shouldCreateUserSuccessfully_whenUserDoesNotExist() {
        // Given
        when(userRepository.findByAuth0Id(testUserDomain.getAuth0Id())).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUserEntity);

        try (MockedStatic<UserEntityConverter> mockedConverter = Mockito.mockStatic(UserEntityConverter.class)) {
            mockedConverter.when(() -> UserEntityConverter.convertToUserEntity(any(User.class)))
                    .thenReturn(testUserEntity);
            mockedConverter.when(() -> UserEntityConverter.convertToUser(any(UserEntity.class)))
                    .thenReturn(testUserDomain);

            // When
            User result = userManager.createUser(testUserDomain);

            // Then
            assertThat(result).isEqualTo(testUserDomain);
            verify(userRepository, times(1)).findByAuth0Id(testUserDomain.getAuth0Id());
            verify(userRepository, times(1)).save(testUserEntity);
            mockedConverter.verify(() -> UserEntityConverter.convertToUserEntity(testUserDomain), times(1));
            mockedConverter.verify(() -> UserEntityConverter.convertToUser(testUserEntity), times(1));
        }
    }

    @Test
    void updateUser_shouldUpdateUserSuccessfully() {
        // Given
        String auth0Id = testUserDomain.getAuth0Id();
        String newFullName = "Updated Name";
        String newHandle = "updated_handle";

        UserEntity updatedEntity = new UserEntity();
        updatedEntity.setId(testUserEntity.getId());
        updatedEntity.setAuth0Id(testUserEntity.getAuth0Id());
        updatedEntity.setFullName(newFullName);
        updatedEntity.setHandle(newHandle);
        updatedEntity.setEmail(testUserEntity.getEmail());

        User updatedDomain = User.builder()
                .id(testUserDomain.getId())
                .auth0Id(testUserDomain.getAuth0Id())
                .fullName(newFullName)
                .handle(newHandle)
                .email(testUserDomain.getEmail())
                .build();

        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(testUserEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedEntity);

        try (MockedStatic<UserEntityConverter> mockedConverter = Mockito.mockStatic(UserEntityConverter.class)) {
            mockedConverter.when(() -> UserEntityConverter.convertToUser(any(UserEntity.class)))
                    .thenReturn(updatedDomain);

            // When
            User result = userManager.updateUser(auth0Id, newFullName, newHandle);

            // Then
            assertThat(result).isEqualTo(updatedDomain);
            verify(userRepository, times(1)).findByAuth0Id(auth0Id);
            verify(userRepository, times(1)).save(testUserEntity);
            mockedConverter.verify(() -> UserEntityConverter.convertToUser(updatedEntity), times(1));
        }
    }

    @Test
    void updateUser_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Given
        String auth0Id = "nonexistent|id";
        String fullName = "New Name";
        String handle = "new_handle";
        when(userRepository.findByAuth0Id(auth0Id)).thenReturn(Optional.empty());

        // When / Then
        HttpServerErrorException exception = assertThrows(HttpServerErrorException.class, () ->
                userManager.updateUser(auth0Id, fullName, handle)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getStatusText()).isEqualTo("User not found");
        verify(userRepository, times(1)).findByAuth0Id(auth0Id);
        verify(userRepository, never()).save(any(UserEntity.class));
    }
} 