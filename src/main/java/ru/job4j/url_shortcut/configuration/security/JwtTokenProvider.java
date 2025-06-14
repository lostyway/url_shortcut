package ru.job4j.url_shortcut.configuration.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Класс отвечает за создание, валидацию и извлечение информации из JWT (JSON Web Tokens) для авторизации.
 */

@Slf4j
@Component
public class JwtTokenProvider {

    /**
     * Секретный ключ для подписи JWT. Сохранен в конфигурации.
     */
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    /**
     * Время жизни токена в миллисекундах
     */
    @Value("${app.jwtExpirationMs}")
    private int jwtExpiration;

    /**
     * @return Генерируем секретный ключ из строки jwtSecret.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Генерирует JWT для аутентифицированного пользователя.
     *
     * @param authentication Объект Authentication, содержащий детали пользователя.
     * @return Сгенерированный JWT.
     */

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder() // Устанавливаем имя пользователя как Subject токена
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date()) // Устанавливаем дату выдачи токена
                .setExpiration(expiryDate) // Устанавливаем срок действия токена
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Подписываем токен с использованием HS256
                .compact(); //Строим и сжимаем токен
    }

    /**
     * Извлекает имя пользователя (Subject) из JWT.
     *
     * @param token JWT
     * @return Имя пользователя
     */
    public String getUsernameFromJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Указываем ключ для парсинга
                .build()
                .parseClaimsJws(token) // Парсим токен
                .getBody()             // Получаем тело токена (claims)
                .getSubject();         // Извлекаем Subject (имя пользователя)
    }

    /**
     * Валидирует JWT.
     * @param authToken JWT для валидации
     * @return true, если токен валиден, иначе false.
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Невалидный JWT токен");
        } catch (ExpiredJwtException ex) {
            log.error("Срок дейсвтия JWT токена истек");
        } catch (UnsupportedJwtException ex) {
            log.error("Неподдерживаемый JWT токен");
        } catch (IllegalArgumentException ex) {
            log.error("JWT строка пуста");
        }
        return false;
    }
}
