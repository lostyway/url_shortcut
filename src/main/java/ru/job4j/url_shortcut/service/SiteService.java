package ru.job4j.url_shortcut.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.url_shortcut.dto.SiteRegistrationResponseDto;
import ru.job4j.url_shortcut.model.User;
import ru.job4j.url_shortcut.repository.SiteRepository;

import static ru.job4j.url_shortcut.utility.GeneratorRandomUtil.generateShortCode;

@Service
@RequiredArgsConstructor
public class SiteService {
    private final SiteRepository repository;
    private final PasswordEncoder encoder;
    private final int LOGIN_LENGTH = 8;
    private final int PASSWORD_LENGTH = 10;

    @Transactional
    public SiteRegistrationResponseDto register(String siteName) {
        var siteFromDb = repository.findBySiteName(siteName);
        if (siteFromDb.isPresent()) {
            return generateSiteRegistrationResponseDto(false, siteFromDb.get().getUsername(), "site already exists");
        } else {
            String login = generateShortCode(LOGIN_LENGTH);
            String password = generateShortCode(PASSWORD_LENGTH);
            String hashedPassword = encoder.encode(password);

            User user = User.builder()
                    .siteName(siteName)
                    .username(login)
                    .password(hashedPassword)
                    .build();

            repository.save(user);
            return generateSiteRegistrationResponseDto(true, login, password);
        }
    }

    private SiteRegistrationResponseDto generateSiteRegistrationResponseDto(boolean success, String login, String password) {
        return SiteRegistrationResponseDto.builder()
                .registrationSuccess(success)
                .login(login)
                .password(password)
                .build();
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
