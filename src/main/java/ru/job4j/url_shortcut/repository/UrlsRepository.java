package ru.job4j.url_shortcut.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.job4j.url_shortcut.model.Urls;

public interface UrlsRepository extends JpaRepository<Urls, Long> {
}
