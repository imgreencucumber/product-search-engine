import index.InvertedIndex;
import index.Trie;
import model.Product;
import service.DataManager;
import service.Indexer;
import service.SearchCore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Тесты для SearchCore

public class SearchCoreTest {
    private SearchCore searchCore;
    private DataManager dataManager;

    @BeforeEach
    public void setUp() {
        InvertedIndex invertedIndex = new InvertedIndex();
        Trie trie = new Trie();
        dataManager = new DataManager();
        Indexer indexer = new Indexer(invertedIndex, trie);

        // Добавление тестовых данных
        dataManager.addProduct(new Product(1, "iPhone 14", "Apple smartphone with great camera", "Electronics"));
        dataManager.addProduct(new Product(2, "Samsung Galaxy", "Android smartphone with AMOLED display", "Electronics"));
        dataManager.addProduct(new Product(3, "MacBook Pro", "Apple laptop with M2 processor", "Computers"));

        // Индексация данных
        indexer.indexProducts(dataManager.getAllProducts().values());

        searchCore = new SearchCore(invertedIndex, trie, dataManager);
    }

    @Test
    public void testKeywordSearch() {
        List<Product> results = searchCore.search("smartphone");
        assertEquals(2, results.size());
        
        results = searchCore.search("Apple");
        assertEquals(2, results.size());
        
        results = searchCore.search("nonexistent");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testPhraseSearch() {
        List<Product> results = searchCore.searchPhrase("great camera");
        assertEquals(1, results.size());
        assertEquals("iPhone 14", results.get(0).getName());
        
        results = searchCore.searchPhrase("nonexistent phrase");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testFuzzySearch() {
        // Тест с точным совпадением
        List<Product> results = searchCore.fuzzySearch("iPhone", 0);
        assertEquals(1, results.size());
        assertEquals("iPhone 14", results.get(0).getName());
        
        // Тест с опечаткой - "iphone" vs "iPhone 14" должно совпадать с расстоянием 2
        results = searchCore.fuzzySearch("iphone", 2);
        assertEquals(1, results.size());
    }

    @Test
    public void testAutocomplete() {
        List<String> suggestions = searchCore.autocomplete("app");
        assertTrue(suggestions.contains("apple"));
        
        suggestions = searchCore.autocomplete("sam");
        assertTrue(suggestions.contains("samsung"));
        
        suggestions = searchCore.autocomplete("xyz");
        assertTrue(suggestions.isEmpty());
    }
}

