package ru.job4j.url_shortcut.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlStatistics {

    @NotNull
    private String url;

    @NotNull
    private long total;
}
