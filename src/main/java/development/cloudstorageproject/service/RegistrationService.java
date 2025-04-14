package development.cloudstorageproject.service;

import development.cloudstorageproject.dto.UserDto;
import development.cloudstorageproject.entity.UserEntity;
import development.cloudstorageproject.enums.Role;
import development.cloudstorageproject.exception.InvalidUserAlreadyExists;
import development.cloudstorageproject.exception.InvalidUserNotFound;
import development.cloudstorageproject.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity registerUser(UserDto user) {
        if (userRepository.findByLogin(user.getLogin()) != null) {
            throw new InvalidUserAlreadyExists("Пользователь уже существует");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(user.getLogin());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.setRole(Role.ROLE_USER.getAuthority());

        return userRepository.save(userEntity);
    }

    public Optional<UserEntity> getUser(String login) {
        return Optional.ofNullable(userRepository.findByLogin(login));
    }

    public void deleteUser(String login) {
        UserEntity user = userRepository.findByLogin(login);
        if (user != null) {
            userRepository.delete(user);
        } else {
            throw new InvalidUserNotFound("Пользователь не найден");
        }
    }
}
