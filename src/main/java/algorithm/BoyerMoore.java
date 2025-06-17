
package algorithm;

import java.util.HashMap;
import java.util.Map;

public class BoyerMoore {

    public static int search(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return 0; // Empty pattern found at the beginning
        }
        if (text == null || text.isEmpty() || pattern.length() > text.length()) {
            return -1; // No match
        }

        Map<Character, Integer> badCharShift = new HashMap<>();
        for (int i = 0; i < pattern.length() - 1; i++) {
            badCharShift.put(pattern.charAt(i), pattern.length() - 1 - i);
        }

        int i = 0; // text index
        while (i <= (text.length() - pattern.length())) {
            int j = pattern.length() - 1; // pattern index

            while (j >= 0 && text.charAt(i + j) == pattern.charAt(j)) {
                j--;
            }

            if (j < 0) {
                return i; // Match found
            } else {
                char badChar = text.charAt(i + j);
                int shift = badCharShift.getOrDefault(badChar, pattern.length());
                i += Math.max(1, shift - (pattern.length() - 1 - j));
            }
        }
        return -1; // No match
    }
}

