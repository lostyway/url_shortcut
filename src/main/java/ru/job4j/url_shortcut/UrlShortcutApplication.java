package ru.job4j.url_shortcut;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@SpringBootApplication
public class UrlShortcutApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrlShortcutApplication.class, args);
        log.info("Application started: http://localhost:8080/swagger-ui/index.html");
    }

    @Bean
    public static BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
