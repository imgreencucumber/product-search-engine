
package algorithm;

import java.util.HashMap;
import java.util.Map;

// Реализация алгоритма Бойера-Мура для поиска подстроки в строке

public class BoyerMoore {

    public static int search(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return 0; // Пустая подстрока найдена в начале
        }
        if (text == null || text.isEmpty() || pattern.length() > text.length()) {
            return -1; // Нет совпадения
        }

        Map<Character, Integer> badCharShift = new HashMap<>();
        for (int i = 0; i < pattern.length() - 1; i++) {
            badCharShift.put(pattern.charAt(i), pattern.length() - 1 - i);
        }

        int i = 0; // индекс текста
        while (i <= (text.length() - pattern.length())) {
            int j = pattern.length() - 1; // индекс подстроки

            while (j >= 0 && text.charAt(i + j) == pattern.charAt(j)) {
                j--;
            }

            if (j < 0) {
                return i; // Совпадение найдено
            } else {
                char badChar = text.charAt(i + j);
                int shift = badCharShift.getOrDefault(badChar, pattern.length());
                i += Math.max(1, shift - (pattern.length() - 1 - j));
            }
        }
        return -1; // Нет совпадения
    }
}

