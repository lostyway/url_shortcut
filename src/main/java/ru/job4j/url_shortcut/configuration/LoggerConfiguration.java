package ru.job4j.url_shortcut.configuration;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggerConfiguration {

    @Around("execution(* ru.job4j.url_shortcut..*(..))")
    public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String params = args.length > 0 ? Arrays.toString(args) : "без параметров";
        log.debug("Вызван метод: {} с параметрами: {}", method, params);

        try {
            Object result = joinPoint.proceed();
            log.debug("Метод {} завершился успешно. Результат: {}", method, result);
            return result;
        } catch (Throwable e) {
            log.error("Метод {} выбросил исключение: {}", method, e.getMessage());
            throw e;
        }
    }
}
