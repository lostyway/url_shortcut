package ru.job4j.url_shortcut.controller;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.job4j.url_shortcut.AbstractIntegrationTest;
import ru.job4j.url_shortcut.dto.SiteRegistrationResponseDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
class LoginAndRegistrationControllerTest extends AbstractIntegrationTest {

    private String testUserLogin;
    private String testUserPassword;

    @BeforeEach
    void setUp() {
        SiteRegistrationResponseDto registrationResponse = siteService.register("test-site-name");
        testUserLogin = registrationResponse.getLogin();
        testUserPassword = registrationResponse.getPassword();
    }

    @Test
    void whenTestRegistrationSuccess() throws Exception {
        String requestBody = "{\"site\": \"test-site\"}";

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registration").value(true))
                .andExpect(jsonPath("$.login").exists())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    void whenTestRegistrationSuccessByDublicateThenFalseRegister() throws Exception {
        String requestBody = "{\"site\": \"test-site\"}";

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registration").value(true))
                .andExpect(jsonPath("$.login").exists())
                .andExpect(jsonPath("$.password").exists());

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registration").value(false))
                .andExpect(jsonPath("$.login").exists())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    void whenTestRegistrationFailureByShortName() throws Exception {
        String requestBody = "{\"site\": \"a\"}";

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.site").value("Имя сайта не соответствует размерам! Минимальный размер: 3, максимальный: 150"));
    }

    @Test
    void whenTestRegistrationFailureByEmptyName() throws Exception {
        String requestBody = "{\"site\": \"\"}";

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.site").value("Имя сайта не соответствует размерам! Минимальный размер: 3, максимальный: 150"));
    }

    @Test
    void whenLoginIsSuccessful() throws Exception {
        String requestBody = "{\"login\": \"" + testUserLogin + "\", \"password\": \"" + testUserPassword + "\"}";

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.not(Matchers.emptyString())));
    }

    @Test
    void whenLoginIsFailureByWrongLogin() throws Exception {
        String requestBody = "{\"login\": \"" + "random" + "\", \"password\": \"" + testUserPassword + "\"}";

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Неверный логин или пароль"));
    }

    @Test
    void whenLoginIsFailureByWrongPassword() throws Exception {
        String requestBody = "{\"login\": \"" + testUserLogin + "\", \"password\": \"" + "passwordWrong" + "\"}";

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Неверный логин или пароль"));
    }
}