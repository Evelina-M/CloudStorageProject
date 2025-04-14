package development.cloudstorageproject.controller;

import development.cloudstorageproject.dto.UserDto;
import development.cloudstorageproject.entity.UserEntity;
import development.cloudstorageproject.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RegistrationControllerTest {
    @Mock
    private RegistrationService registrationService;
    private RegistrationController registrationController;
    private final String LOGIN = "Nikola";
    private final String PASSWORD = "user3";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registrationController = new RegistrationController(registrationService);
    }

    @Test
    void registerUser_Success() {
        UserDto userDto = new UserDto(LOGIN, PASSWORD);
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(LOGIN);
        userEntity.setPassword(PASSWORD);

        when(registrationService.registerUser(any(UserDto.class))).thenReturn(userEntity);

        ResponseEntity<UserEntity> response = registrationController.register(userDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userEntity, response.getBody());
        verify(registrationService, times(1)).registerUser(userDto);
    }

    @Test
    void getUser_Success() {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(LOGIN);
        userEntity.setPassword(PASSWORD);

        when(registrationService.getUser(LOGIN)).thenReturn(Optional.of(userEntity));

        ResponseEntity<Optional<UserEntity>> response = registrationController.getUser(LOGIN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Optional.of(userEntity), response.getBody());
        verify(registrationService, times(1)).getUser(LOGIN);
    }

    @Test
    void deleteUser_Success() {
        ResponseEntity<String> response = registrationController.deleteUser(LOGIN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(registrationService, times(1)).deleteUser(LOGIN);
    }
}
