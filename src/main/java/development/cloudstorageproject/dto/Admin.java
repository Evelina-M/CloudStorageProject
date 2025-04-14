package development.cloudstorageproject.dto;

import development.cloudstorageproject.repository.UserRepository;
import development.cloudstorageproject.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Admin implements CommandLineRunner {
    private final AdminService adminService;
    private final UserRepository userRepository;

    @Value("${admin.adminLogin}")
    private String adminLogin;

    @Value("${admin.adminPassword}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.findByLogin(adminLogin) == null) {
            adminService.createAdminUser(adminLogin, adminPassword);
        }
    }
}
