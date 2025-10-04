package vn.iotstar;

import vn.iotstar.entity.Role;
import vn.iotstar.entity.Users;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@org.springframework.context.annotation.Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner init(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_ADMIN")));
            Role userRole = roleRepo.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_USER")));

            // Tạo admin
            if (userRepo.findByUsername("admin").isEmpty()) {
                Users admin = new Users();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(encoder.encode("123456"));
                admin.setRoles(Set.of(adminRole));
                userRepo.save(admin);
            }

            // Tạo user thường
            if (userRepo.findByUsername("user").isEmpty()) {
                Users user = new Users();
                user.setUsername("user");
                user.setEmail("user@example.com");
                user.setPassword(encoder.encode("123456"));
                user.setRoles(Set.of(userRole)); // chỉ ROLE_USER
                userRepo.save(user);
            }
        };
    }
}

