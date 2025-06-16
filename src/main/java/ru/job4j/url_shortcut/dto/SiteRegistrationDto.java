package ru.job4j.url_shortcut.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SiteRegistrationDto {
    @Size(min = 3, max = 150, message = "Имя сайта не соответствует размерам! Минимальный размер: 3, максимальный: 150")
    private String site;
}
