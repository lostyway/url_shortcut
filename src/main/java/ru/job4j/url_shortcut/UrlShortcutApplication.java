package ru.job4j.url_shortcut;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootApplication
public class UrlShortcutApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlShortcutApplication.class, args);
        log.info("Application started: http://localhost:8080/swagger-ui/index.html");
    }

    /**
     * Создает бин для кодирования паролей.
     * Используется BCryptPasswordEncoder, который является рекомендуемым алгоритмом.
     *
     * @return Экземпляр BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
