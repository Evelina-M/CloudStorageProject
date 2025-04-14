package development.cloudstorageproject.service;

import development.cloudstorageproject.dto.UserDto;
import development.cloudstorageproject.entity.UserEntity;
import development.cloudstorageproject.exception.InvalidUserNameOrPassword;
import development.cloudstorageproject.exception.TokenNotFoundException;
import development.cloudstorageproject.repository.UserRepository;
import development.cloudstorageproject.security.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtToken jwtToken;
    public Map<String, String> tokenWhiteList = new HashMap<>();

    public String login(UserDto userDto) {
        UserEntity user = userRepository.findByLogin(userDto.getLogin());

        if (user != null && passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            String token = jwtToken.generateToken(user);
            tokenWhiteList.put(user.getLogin(), token);
            return token;
        } else {
            throw new InvalidUserNameOrPassword("Неверное имя пользователя или пароль");
        }
    }

    public void logout(String authToken) {
        if (tokenWhiteList.containsKey(authToken)) {
            tokenWhiteList.remove(authToken);
        } else {
            throw new TokenNotFoundException("Токен не найден в белом списке");
        }
    }
}
