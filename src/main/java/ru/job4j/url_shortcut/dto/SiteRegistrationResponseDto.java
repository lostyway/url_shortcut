package ru.job4j.url_shortcut.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SiteRegistrationResponseDto {

    private boolean registrationSuccess;

    private String login;

    private String password;
}
