
package index;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

// Реализация инвертированного индекса для поиска по ключевым словам

public class InvertedIndex {
    private Map<String, Set<Integer>> index;

    public InvertedIndex() {
        this.index = new HashMap<>();
    }

    public void addDocument(String text, int documentId) {
        String[] words = text.toLowerCase().split("\\W+");
        for (String word : words) {
            if (!word.isEmpty()) {
                index.computeIfAbsent(word, k -> new HashSet<>()).add(documentId);
            }
        }
    }

    public Set<Integer> search(String query) {
        String[] words = query.toLowerCase().split("\\W+");
        if (words.length == 0) {
            return new HashSet<>();
        }

        Set<Integer> result = null;
        for (String word : words) {
            if (!word.isEmpty()) {
                Set<Integer> documents = index.get(word);
                if (documents == null) {
                    return new HashSet<>(); // Если какое-либо слово не найдено, пересечение невозможно, возвращаем пустое множество
                }
                if (result == null) {
                    result = new HashSet<>(documents);
                } else {
                    result.retainAll(documents);
                }
            }
        }
        return result != null ? result : new HashSet<>();
    }

    public Map<String, Set<Integer>> getIndex() {
        return index;
    }
}

