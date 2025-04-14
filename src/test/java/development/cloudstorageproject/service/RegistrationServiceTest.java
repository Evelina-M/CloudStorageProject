package development.cloudstorageproject.service;

import development.cloudstorageproject.dto.UserDto;
import development.cloudstorageproject.entity.UserEntity;
import development.cloudstorageproject.exception.InvalidUserAlreadyExists;
import development.cloudstorageproject.exception.InvalidUserNotFound;
import development.cloudstorageproject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RegistrationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";

    private UserDto userDto;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(USERNAME, PASSWORD);

        userEntity = new UserEntity();
        userEntity.setLogin(USERNAME);
        userEntity.setPassword(ENCODED_PASSWORD);
    }

    @Test
    void registerUser_success() {
        when(userRepository.findByLogin(USERNAME)).thenReturn(null);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserEntity registeredUser = registrationService.registerUser(userDto);

        assertNotNull(registeredUser);
        assertEquals(USERNAME, registeredUser.getLogin());
        assertEquals(ENCODED_PASSWORD, registeredUser.getPassword());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void registerUser_userAlreadyExists() {
        when(userRepository.findByLogin(USERNAME)).thenReturn(userEntity);
        assertThrows(InvalidUserAlreadyExists.class, () -> registrationService.registerUser(userDto));
    }

    @Test
    void getUser_userExists() {
        when(userRepository.findByLogin(USERNAME)).thenReturn(userEntity);
        Optional<UserEntity> result = registrationService.getUser(USERNAME);

        assertTrue(result.isPresent());
        assertEquals(userEntity, result.get());
    }

    @Test
    void getUser_userNotFound() {
        when(userRepository.findByLogin(USERNAME)).thenReturn(null);
        Optional<UserEntity> result = registrationService.getUser(USERNAME);
        assertFalse(result.isPresent());
    }

    @Test
    void deleteUser_userExists() {
        when(userRepository.findByLogin(USERNAME)).thenReturn(userEntity);
        registrationService.deleteUser(USERNAME);
        verify(userRepository).delete(userEntity);
    }

    @Test
    void deleteUser_userNotFound() {
        when(userRepository.findByLogin(USERNAME)).thenReturn(null);
        assertThrows(InvalidUserNotFound.class, () -> registrationService.deleteUser(USERNAME));
    }
}
