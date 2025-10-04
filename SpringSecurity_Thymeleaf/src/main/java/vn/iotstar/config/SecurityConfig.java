package vn.iotstar.config;

import vn.iotstar.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    // ===== COMMON BEANS =====
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService inMemoryUserDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("123456"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password(encoder.encode("123456"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider(PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    // ===== FILTER CHAIN =====
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Cho phép public cho API users
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll()   // ✅ tất cả API public
                .requestMatchers("/", "/index", "/login", "/css/**", "/js/**").permitAll()
                .requestMatchers("/demo1/hello").permitAll()
                .requestMatchers("/demo1/customers").authenticated()
                .requestMatchers("/demo1/admin").hasRole("ADMIN")
                .requestMatchers("/products/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // Tắt CSRF cho phép POST JSON
            .csrf(csrf -> csrf.disable())
            // Dùng form login cho giao diện web
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            // Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    // ===== AUTH MANAGER =====
    @Bean
    public AuthenticationManager authManager(HttpSecurity http,
                                             UserDetailsService inMemoryUserDetailsService,
                                             DaoAuthenticationProvider daoAuthProvider,
                                             PasswordEncoder encoder) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(inMemoryUserDetailsService).passwordEncoder(encoder);
        authBuilder.authenticationProvider(daoAuthProvider);
        return authBuilder.build();
    }
}
