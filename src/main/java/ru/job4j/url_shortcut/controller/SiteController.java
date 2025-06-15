package ru.job4j.url_shortcut.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j.url_shortcut.dto.ConvertDto;
import ru.job4j.url_shortcut.model.Url;
import ru.job4j.url_shortcut.service.UrlService;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SiteController {
    private final UrlService urlService;

    @PostMapping("/convert")
    public ResponseEntity<?> convert(@Valid @RequestBody ConvertDto request) {
        String url = request.getUrl();

        var existingUrl = urlService.findByUrl(url);
        if (existingUrl.isPresent()) {
            return ResponseEntity.ok(Map.of("code", existingUrl.get().getCode()));
        }

        String code = urlService.registerAndConvertUrl(url);
        return ResponseEntity.ok(Map.of("code", code));
    }

    @GetMapping("/redirect/{code}")
    public ResponseEntity<Void> getRedirectUrl(@PathVariable String code) {
        Url url = urlService.findAndIncrementByCode(code);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url.getUrl()));

        return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
    }

    @GetMapping("/statistic")
    public ResponseEntity<?> getStatistic() {
        return ResponseEntity.ok(urlService.getStatisticByUrl());
    }
}
