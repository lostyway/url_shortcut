package ru.job4j.url_shortcut.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "urls")
@Data
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "url", unique = true, nullable = false)
    private String url;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "request_count")
    private long requestCount;

}
