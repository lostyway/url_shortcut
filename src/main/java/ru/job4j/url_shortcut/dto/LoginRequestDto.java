package ru.job4j.url_shortcut.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDto {
    @NotBlank
    private String login;

    @NotBlank
    private String password;
}
