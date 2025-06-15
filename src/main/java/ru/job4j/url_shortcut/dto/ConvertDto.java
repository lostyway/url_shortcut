package ru.job4j.url_shortcut.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConvertDto {
    @NotBlank(message = "URL cannot be empty")
    private String url;
}
