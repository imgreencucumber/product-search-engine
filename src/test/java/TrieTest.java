import index.Trie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrieTest {
    private Trie trie;

    @BeforeEach
    public void setUp() {
        trie = new Trie();
    }

    @Test
    public void testInsertAndAutocomplete() {
        trie.insert("apple");
        trie.insert("application");
        trie.insert("apply");
        trie.insert("banana");

        List<String> results = trie.autocomplete("app");
        assertEquals(3, results.size());
        assertTrue(results.contains("apple"));
        assertTrue(results.contains("application"));
        assertTrue(results.contains("apply"));
    }

    @Test
    public void testAutocompleteNoMatches() {
        trie.insert("apple");
        trie.insert("banana");

        List<String> results = trie.autocomplete("xyz");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testAutocompleteExactMatch() {
        trie.insert("apple");
        trie.insert("application");

        List<String> results = trie.autocomplete("apple");
        assertTrue(results.contains("apple"));
        // "application" does NOT start with "apple" - it starts with "appl"
        // So we should only expect "apple" in the results
        assertEquals(1, results.size());
    }

    @Test
    public void testEmptyPrefix() {
        trie.insert("apple");
        trie.insert("banana");

        List<String> results = trie.autocomplete("");
        assertEquals(2, results.size());
        assertTrue(results.contains("apple"));
        assertTrue(results.contains("banana"));
    }

    @Test
    public void testSingleCharacterPrefix() {
        trie.insert("apple");
        trie.insert("application");
        trie.insert("banana");

        List<String> results = trie.autocomplete("a");
        assertEquals(2, results.size());
        assertTrue(results.contains("apple"));
        assertTrue(results.contains("application"));
    }
}

