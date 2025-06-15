package ru.job4j.url_shortcut.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank
    private String login;

    @NotBlank
    private String password;
}
