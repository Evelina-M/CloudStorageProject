package development.cloudstorageproject.service;

import development.cloudstorageproject.dto.UserDto;
import development.cloudstorageproject.entity.UserEntity;
import development.cloudstorageproject.exception.InvalidUserNameOrPassword;
import development.cloudstorageproject.exception.TokenNotFoundException;
import development.cloudstorageproject.repository.UserRepository;
import development.cloudstorageproject.security.JwtToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtToken jwtToken;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String TOKEN = "token";

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
    void login_success() {
        userEntity.setPassword(passwordEncoder.encode(PASSWORD));
        when(userRepository.findByLogin(USERNAME)).thenReturn(userEntity);
        when(passwordEncoder.matches(userDto.getPassword(), userEntity.getPassword())).thenReturn(true);
        when(jwtToken.generateToken(userEntity)).thenReturn(TOKEN);

        String resultToken = authService.login(userDto);

        assertEquals(TOKEN, resultToken);
        assertTrue(authService.tokenWhiteList.containsKey(USERNAME));
        assertEquals(TOKEN, authService.tokenWhiteList.get(USERNAME));
    }

    @Test
    void login_invalidPassword() {
        when(userRepository.findByLogin(USERNAME)).thenReturn(userEntity);
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);
        assertThrows(InvalidUserNameOrPassword.class, () -> authService.login(userDto));
    }

    @Test
    void login_userNotFound() {
        when(userRepository.findByLogin(USERNAME)).thenReturn(null);
        assertThrows(InvalidUserNameOrPassword.class, () -> authService.login(userDto));
    }

    @Test
    void logout_success() {
        authService.tokenWhiteList.put(TOKEN, USERNAME);
        authService.logout(TOKEN);
        assertFalse(authService.tokenWhiteList.containsKey(TOKEN));
        assertFalse(authService.tokenWhiteList.containsValue(USERNAME));
    }

    @Test
    void logout_tokenNotFound() {
        assertThrows(TokenNotFoundException.class, () -> authService.logout(TOKEN));
    }
}
