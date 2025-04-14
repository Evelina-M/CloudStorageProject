package development.cloudstorageproject.service;

import development.cloudstorageproject.entity.UserEntity;
import development.cloudstorageproject.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createAdminUser(String login, String password) {
        if (this.passwordEncoder == null) {
            throw new IllegalStateException("PasswordEncoder не инициализирован");
        }
        UserEntity user = new UserEntity();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_ADMIN");

        userRepository.save(user);
    }
}