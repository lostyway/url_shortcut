package ru.job4j.url_shortcut.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConvertDto {
    @NotBlank(message = "Ссылка не может быть пуста!")
    private String url;
}
