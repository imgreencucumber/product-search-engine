
package index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Реализация Trie-дерева для автодополнения

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
        }
        current.isEndOfWord = true;
    }

    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode current = root;
        for (char ch : prefix.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) {
                return results; // Нет слов с этим префиксом
            }
        }
        findAllWords(current, prefix, results);
        return results;
    }

    private void findAllWords(TrieNode node, String currentPrefix, List<String> results) {
        if (node.isEndOfWord) {
            results.add(currentPrefix);
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            findAllWords(entry.getValue(), currentPrefix + entry.getKey(), results);
        }
    }

    private static class TrieNode {
        Map<Character, TrieNode> children;
        boolean isEndOfWord;

        public TrieNode() {
            children = new HashMap<>();
            isEndOfWord = false;
        }
    }
}

