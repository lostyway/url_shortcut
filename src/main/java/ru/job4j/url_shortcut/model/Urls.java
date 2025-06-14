package ru.job4j.url_shortcut.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "urls")
@Data
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class Urls {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "original_url", unique = true, nullable = false)
    private String originalUrl;

    @Column(name = "short_url", unique = true)
    private String shortUrl;

    @NotNull
    @Column(name = "request_count")
    private long requestCount;

}
