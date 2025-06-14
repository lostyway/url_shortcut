package ru.job4j.url_shortcut.configuration.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Этот фильтр будет перехватывать каждый запрос,
 * проверять наличие JWT в заголовке Authorization, валидировать его и, если токен действителен,
 * устанавливать контекст безопасности Spring.
 *
 * @implNote OncePerRequestFilter -> Это базовый класс Spring, который гарантирует, ч
 * то фильтр будет выполняться только один раз для каждого запроса, независимо от количества других фильтров в цепочке.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Метод, выполняющий один раз для каждого входящего HTTP запроса.
     * Он извлекает JWT из заголовка, валидирует его и устанавливает аутентификацию в SecurityContextHolder.
     *
     * @param request     HTTP запрос.
     * @param response    HTTP ответ.
     * @param filterChain Цепочка фильтров.
     *                    <p>
     *                    UsernamePasswordAuthenticationToken authentication = ...: Создает объект Authentication.
     *                    <p>
     *                    Первый аргумент (userDetails) - это Principal (тот, кто пытается аутентифицироваться).
     *                    <p>
     *                    Второй аргумент (null) - это Credentials (пароль). Для JWT аутентификации пароль не нужен,
     *                    так как токен уже является подтверждением.
     *                    <p>
     *                    Третий аргумент (userDetails.getAuthorities()) - это список ролей/авторитетов пользователя.
     *                    <p>
     *                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));:
     *                    <p>
     *                    Устанавливает дополнительные детали аутентификации, такие как IP-адрес клиента и идентификатор сессии
     *                    </p>
     */

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromJWT(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Не удалось установить аутентификацию пользователя", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает JWT из хедера Authorization.
     *
     * @param request HTTP запрос.
     * @return JWT строка или null, если не найдена.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Извлекаем токен после "Bearer "
        }
        return null;
    }
}
