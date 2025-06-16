package ru.job4j.url_shortcut.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;
import ru.job4j.url_shortcut.AbstractIntegrationTest;
import ru.job4j.url_shortcut.dto.LoginRequestDto;
import ru.job4j.url_shortcut.dto.SiteRegistrationResponseDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SiteControllerTest extends AbstractIntegrationTest {

    @Autowired
    private LoginAndRegistrationController loginController;

    private String bearerToken;

    private void generateToken() {
        String login;
        String password;
        SiteRegistrationResponseDto registrationResponse = siteService.register("test-register-test-name" + System.currentTimeMillis());
        login = registrationResponse.getLogin();
        password = registrationResponse.getPassword();

        LoginRequestDto dto = LoginRequestDto.builder()
                .login(login)
                .password(password)
                .build();

        ResponseEntity<String> response = loginController.login(dto);
        this.bearerToken = response.getBody();
    }

    @BeforeEach
    void setUpMain() {
        generateToken();
    }


    @Test
    @DisplayName("Конвертация сайта выполнена успешно")
    void whenTestConvertSuccess() throws Exception {
        String input = "{\"url\": \"https://www.youtube.com/watch?v=nkHqNcyc3f4\"}";

        mockMvc.perform(post("/convert")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNotEmpty());
    }

    @Test
    @DisplayName("Конвертация сайта и его дублирование выполнено успешно")
    void whenTestConvertSuccessAndDublicateThenSuccess() throws Exception {
        String input = "{\"url\": \"https://www.youtube.com/watch?v=nkHqNcyc3f4\"}";

        MvcResult firstResult = mockMvc.perform(post("/convert")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andReturn();

        String firstResponseBody = firstResult.getResponse().getContentAsString();

        String firstCode = JsonPath.read(firstResponseBody, "$.code");

        MvcResult secondResult = mockMvc.perform(post("/convert")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").isNotEmpty())
                .andReturn();

        String secondResponseBody = secondResult.getResponse().getContentAsString();

        String secondCode = JsonPath.read(secondResponseBody, "$.code");

        assertThat(firstCode).isEqualTo(secondCode);
    }

    @Test
    @DisplayName("Конвертация сайта не выполнено из-за пустого url")
    void whenTestConvertFailByEmptyUrl() throws Exception {
        String input = "{\"url\": \"\"}";

        mockMvc.perform(post("/convert")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").value("Ссылка не может быть пуста!"));
    }

    @Nested
    @DisplayName("Тестирование сценариев редиректа")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class RedirectTest {
        private String generatedCode;
        private final String originalUrl = "https://www.youtube.com/watch?v=nkHqNcyc3f4";

        @BeforeAll
        void setUp() throws Exception {
            generateToken();

            String input = "{\"url\": \"" + originalUrl + "\"}";
            MvcResult result = mockMvc.perform(post("/convert")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(input))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").isNotEmpty())
                    .andReturn();

            generatedCode = JsonPath.read(result.getResponse().getContentAsString(), "$.code");
            assertThat(generatedCode).isNotNull().isNotEmpty();
        }


        @Test
        @DisplayName("Успешный редирект по сгенерированному коду")
        void whenRedirectByCodeSuccess() throws Exception {
            mockMvc.perform(get("/redirect/" + generatedCode))
                    .andDo(print())
                    .andExpect(status().isSeeOther())
                    .andExpect(header().string("Location", originalUrl));
        }

        @Test
        @DisplayName("Редирект по несуществующему коду")
        void whenRedirectByWrongCodeThenReturnNotFoundAndMessage() throws Exception {
            String codeWrong = "codeWrong";
            mockMvc.perform(get("/redirect/" + codeWrong))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Не было найдено адреса с этим кодом: " + codeWrong));
        }

        @Test
        @DisplayName("Редирект по пустому коду")
        void whenRedirectByEmptyCodeThenGetBadRequestAndMessage() throws Exception {
            mockMvc.perform(get("/redirect/"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Были переданы неверные значения (пустые, некорректные)"));
        }

        @Test
        @DisplayName("Редирект по коду с одним пробелом")
        void whenRedirectByEmptyCodeWithSpaceThenGetBadRequestAndMessage() throws Exception {
            mockMvc.perform(get("/redirect/ "))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Код не может быть пустым!"));
        }
    }

    @Nested
    @DisplayName("Тестирование сценариев статистики")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class StatisticTest {
        private String generatedCode;
        private final String bankUrl = "https://www.cbr.ru/currency_base/dynamics/?UniDbQuery.Posted=True&UniDbQuery.VAL_NM_RQ=r01235";
        private final String youtubeUrl = "https://www.youtube.com/";
        String inputForBankUrl = "{\"url\": \"" + bankUrl + "\"}";
        String codeForBankUrl;

        String inputForYoutubeUrl = "{\"url\": \"" + youtubeUrl + "\"}";
        String codeForYoutubeUrl;

        @BeforeAll
        void setUp() throws Exception {
            generateToken();

            MvcResult resultBank = mockMvc.perform(post("/convert")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inputForBankUrl))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").isNotEmpty())
                    .andReturn();

            codeForBankUrl = JsonPath.read(resultBank.getResponse().getContentAsString(), "$.code");
            assertThat(codeForBankUrl).isNotNull().isNotEmpty();

            MvcResult resultYoutube = mockMvc.perform(post("/convert")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(inputForYoutubeUrl))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").isNotEmpty())
                    .andReturn();

            codeForYoutubeUrl = JsonPath.read(resultYoutube.getResponse().getContentAsString(), "$.code");
            assertThat(codeForYoutubeUrl).isNotNull().isNotEmpty();
        }

        private void makeRedirect(String code, String url) throws Exception {
            mockMvc.perform(get("/redirect/" + code))
                    .andDo(print())
                    .andExpect(status().isSeeOther())
                    .andExpect(header().string("Location", url));
        }

        @Test
        @DisplayName("Проверка статистики по 10 редиректам на сайт ЦБ")
        void whenRedirectIsSuccess() throws Exception {
            int countForBankUrl = 10;
            int countForYoutubeUrl = 20;
            for (int i = 0; i < countForBankUrl; i++) {
                makeRedirect(codeForBankUrl, bankUrl);
            }

            for (int i = 0; i < countForYoutubeUrl; i++) {
                makeRedirect(codeForYoutubeUrl, youtubeUrl);
            }

            mockMvc.perform(get("/statistic"))
                    .andDo(print())
                    .andExpect(jsonPath("$[?(@.url == '" + bankUrl + "')].total").value(countForBankUrl))
                    .andExpect(jsonPath("$[?(@.url == '" + youtubeUrl + "')].total").value(countForYoutubeUrl));
        }

        @Test
        @DisplayName("Проверка статистики по редиректам дубль")
        void whenRedirectIsSuccessSecond() throws Exception {
            int countForBankUrl = 20;
            int countForYoutubeUrl = 30;
            for (int i = 0; i < countForBankUrl; i++) {
                makeRedirect(codeForBankUrl, bankUrl);
            }

            for (int i = 0; i < countForYoutubeUrl; i++) {
                makeRedirect(codeForYoutubeUrl, youtubeUrl);
            }

            mockMvc.perform(get("/statistic"))
                    .andDo(print())
                    .andExpect(jsonPath("$[?(@.url == '" + bankUrl + "')].total").value(countForBankUrl))
                    .andExpect(jsonPath("$[?(@.url == '" + youtubeUrl + "')].total").value(countForYoutubeUrl));
        }
    }
}