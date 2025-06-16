package ru.job4j.url_shortcut.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "username"})
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "site")
    private String siteName;

    @NotNull
    @Column(unique = true, nullable = false)
    @Size(min = 3, max = 50)
    private String username;

    @NotNull
    @Column(nullable = false)
    @Size(min = 3, max = 100)
    private String password;
}
