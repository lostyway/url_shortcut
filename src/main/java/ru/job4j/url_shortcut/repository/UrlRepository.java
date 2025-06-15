package ru.job4j.url_shortcut.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.job4j.url_shortcut.model.Url;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByCode(String shortcut);

    Optional<Url> findByUrl(String url);
}
