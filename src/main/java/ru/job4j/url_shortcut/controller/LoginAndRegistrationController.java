package ru.job4j.url_shortcut.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.url_shortcut.configuration.security.JwtTokenProvider;
import ru.job4j.url_shortcut.dto.LoginRequestDto;
import ru.job4j.url_shortcut.dto.SiteRegistrationDto;
import ru.job4j.url_shortcut.dto.SiteRegistrationResponseDto;
import ru.job4j.url_shortcut.service.SiteService;

import java.util.Map;


@RequiredArgsConstructor
@RestController
public class LoginAndRegistrationController {
    private final SiteService siteService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/registration")
    public ResponseEntity<?> register(@RequestBody SiteRegistrationDto dto) {
        String siteName = dto.getSite();
        SiteRegistrationResponseDto result = siteService.register(siteName);
        return ResponseEntity.ok(Map.of(
                "registration", result.isRegistrationSuccess(),
                "login", result.getLogin(),
                "password", result.getPassword()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getLogin(), loginDto.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtTokenProvider.generateToken(authentication);

            return ResponseEntity.ok(jwt);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password", e);
        }
    }
}
