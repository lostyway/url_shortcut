package ru.job4j.url_shortcut.utility;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class GeneratorRandomUtil {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    /**
     * Метод генерирует уникальный код в Base62 формате заданной длины.
     *
     * @param length Желаемая длина генерируемого кода.
     * @return Случайный короткий Base62 код.
     */
    public static String generateShortCode(int length) {
        StringBuilder shortCode = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < length; i++) {
            shortCode.append(BASE62_ALPHABET.charAt(random.nextInt(BASE)));
        }

        return shortCode.toString();
    }
}
