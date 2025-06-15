package ru.job4j.url_shortcut.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.url_shortcut.dto.UrlStatistics;
import ru.job4j.url_shortcut.model.Url;
import ru.job4j.url_shortcut.repository.UrlRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.job4j.url_shortcut.utility.GeneratorRandomUtil.generateShortCode;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlsRepository;
    private final int CODE_LENGTH = 8;
    private final EntityManager entityManager;

    @Transactional
    public Url findAndIncrementByCode(String code) {
        Url url = urlsRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("No url found with code " + code));

        try {
            entityManager.createNativeQuery("call increment_url_request_count_by_code(:code)")
                    .setParameter("code", code)
                    .executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to call increment_url_request_count_by_code: " + code, e);
        }

        return url;
    }

    @Transactional(readOnly = true)
    public Optional<Url> findByUrl(String url) {
        return urlsRepository.findByUrl(url);
    }

    @Transactional
    public String registerAndConvertUrl(String url) {
        String code = generateShortCode(CODE_LENGTH);

        Url urlToSave = Url.builder()
                .code(code)
                .url(url)
                .requestCount(0)
                .build();

        urlsRepository.save(urlToSave);
        return code;
    }

    @Transactional(readOnly = true)
    public List<UrlStatistics> getStatisticByUrl() {
        return urlsRepository.findAll().stream()
                .map(url -> new UrlStatistics(url.getUrl(), url.getRequestCount()))
                .collect(Collectors.toList());
    }
}
