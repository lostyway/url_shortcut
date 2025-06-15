package ru.job4j.url_shortcut.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.job4j.url_shortcut.model.User;

import java.util.Optional;

public interface SiteRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findBySiteName(String siteName);
}
