package ru.job4j.url_shortcut.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransactionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionController {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Были переданы неправильные параметры. Произошло исключение: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Были переданы неправильные параметры");
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<String> handleTransactionException(TransactionException ex) {
        log.error("Во время выполнения транзакции произошла ошибка: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Операция прошла неудачно. Попробуйте позже");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.error("Пользователь не был найден. Произошло исключение: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не был найден");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Были переданы неверные данные. Произошло исключение: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Были переданы неверные данные");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        log.error("Перехвачено ResponseStatusException. Статус: {}, Причина: {}", ex.getStatusCode(), ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleResponseStatusException(NoResourceFoundException ex) {
        log.error("Перехвачено NoResourceFoundException. Причина: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Были переданы неверные значения (пустые, некорректные)");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> error.getDefaultMessage()
                ));

        log.error("Перехвачено MethodArgumentNotValidException. Ошибка валидации: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleAllThrowableException(Throwable ex) {
        log.error("Произошло глобальное исключение: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка внутри сервера");
    }
}
