package development.cloudstorageproject.controller;

import development.cloudstorageproject.dto.UserDto;
import development.cloudstorageproject.entity.UserEntity;
import development.cloudstorageproject.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@RequestBody UserDto user) {
        return new ResponseEntity<>(registrationService.registerUser(user), HttpStatus.OK);
    }

    @GetMapping("/{login}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Optional<UserEntity>> getUser(@PathVariable String login) {
        return new ResponseEntity<>(registrationService.getUser(login), HttpStatus.OK);
    }

    @DeleteMapping("/{login}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String login) {
        registrationService.deleteUser(login);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
