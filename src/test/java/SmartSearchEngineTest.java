import index.InvertedIndex;
import index.Trie;
import model.Product;
import model.SearchResult;
import service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Тесты для SmartSearchEngine

public class SmartSearchEngineTest {
    
    private SmartSearchEngine smartSearchEngine;
    private DataManager dataManager;
    
    @BeforeEach
    void setUp() {
        // Инициализация компонентов
        InvertedIndex invertedIndex = new InvertedIndex();
        Trie trie = new Trie();
        dataManager = new DataManager();
        Indexer indexer = new Indexer(invertedIndex, trie);
        
        // Добавление тестовых данных
        addTestData();
        
        // Индексация данных
        indexer.indexProducts(dataManager.getAllProducts().values());
        
        // Инициализация smart search engine
        smartSearchEngine = new SmartSearchEngine(invertedIndex, trie, dataManager);
    }
    
    private void addTestData() {
        dataManager.addProduct(new Product(1, "iPhone 14", "Apple smartphone iPhone 14 with advanced camera", "Electronics"));
        dataManager.addProduct(new Product(2, "Samsung Galaxy S23", "Samsung flagship smartphone with great camera", "Electronics"));
        dataManager.addProduct(new Product(3, "MacBook Pro", "Apple laptop MacBook Pro with M2 chip", "Computers"));
        dataManager.addProduct(new Product(4, "iPad Pro", "Apple tablet iPad Pro with Liquid Retina display", "Tablets"));
        dataManager.addProduct(new Product(5, "AirPods Pro", "Apple wireless earbuds with noise cancellation", "Accessories"));
    }
    
    @Test
    void testSmartSearchBasicKeyword() {
        List<SearchResult> results = smartSearchEngine.smartSearch("iPhone");
        
        assertFalse(results.isEmpty(), "Should find iPhone products");
        assertTrue(results.get(0).getProduct().getName().contains("iPhone"), 
                   "First result should contain iPhone");
        assertTrue(results.get(0).getRelevanceScore() > 0, "Should have positive relevance score");
    }
    
    @Test
    void testSmartSearchMultipleKeywords() {
        List<SearchResult> results = smartSearchEngine.smartSearch("Apple smartphone");
        
        assertFalse(results.isEmpty(), "Should find Apple smartphone products");
        assertTrue(results.stream().anyMatch(r -> r.getProduct().getName().contains("iPhone")), 
                   "Should find iPhone in results");
    }
    
    @Test
    void testSmartSearchExactPhrase() {
        List<SearchResult> results = smartSearchEngine.smartSearch("\"noise cancellation\"");
        
        assertFalse(results.isEmpty(), "Should find products with exact phrase");
        assertTrue(results.stream().anyMatch(r -> 
                   r.getProduct().getDescription().contains("noise cancellation")), 
                   "Should find products with noise cancellation");
    }
    
    @Test
    void testSmartSearchFuzzySearch() {
        // Тест с опечаткой - "Smasung" вместо "Samsung"
        List<SearchResult> results = smartSearchEngine.smartSearch("Smasung");
        
        assertFalse(results.isEmpty(), "Should find Samsung products despite typo");
        assertTrue(results.stream().anyMatch(r -> r.getProduct().getName().contains("Samsung")), 
                   "Should find Samsung in results despite typo");
    }
    
    @Test
    void testEmptyQuery() {
        List<SearchResult> results = smartSearchEngine.smartSearch("");
        assertTrue(results.isEmpty(), "Empty query should return no results");
        
        results = smartSearchEngine.smartSearch(null);
        assertTrue(results.isEmpty(), "Null query should return no results");
    }
    
    @Test
    void testRelevanceScoring() {
        List<SearchResult> results = smartSearchEngine.smartSearch("Apple");
        
        // Должны найти несколько продуктов Apple
        assertTrue(results.size() > 1, "Should find multiple Apple products");
        
        // Результаты должны быть отсортированы по релевантности (убывающий порядок)
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getRelevanceScore() >= results.get(i + 1).getRelevanceScore(),
                      "Results should be sorted by relevance score (descending)");
        }
    }
    
    @Test
    void testIntelligentAutocomplete() {
        List<String> suggestions = smartSearchEngine.getSearchSuggestions("app");
        
        assertFalse(suggestions.isEmpty(), "Should provide autocomplete suggestions");
        assertTrue(suggestions.stream().anyMatch(s -> s.startsWith("app") || s.contains("app")), 
                   "Suggestions should be related to 'app'");
    }
    
    @Test
    void testSearchAnalytics() {
        SearchAnalytics analytics = smartSearchEngine.getSearchAnalytics("iPhone");
        
        assertNotNull(analytics, "Analytics should not be null");
        assertNotNull(analytics.getQueryIntent(), "Query intent should not be null");
        assertTrue(analytics.getTotalMatches() >= 0, "Total matches should be non-negative");
        assertNotNull(analytics.getSearchStrategy(), "Search strategy should not be null");
    }
    
    @Test
    void testComplexQuery() {
        List<SearchResult> results = smartSearchEngine.smartSearch("Apple smartphone camera");
        
        assertFalse(results.isEmpty(), "Should find results for complex query");
        
        // Выше приоритет продуктов, которые соответствуют нескольким терминам
        SearchResult topResult = results.get(0);
        String topProductText = (topResult.getProduct().getName() + " " + 
                               topResult.getProduct().getDescription()).toLowerCase();
        
        assertTrue(topProductText.contains("apple") || topProductText.contains("camera"), 
                   "Top result should match key terms from query");
    }
    
    @Test
    void testMatchTypeClassification() {
        List<SearchResult> results = smartSearchEngine.smartSearch("iPhone 14");
        
        assertFalse(results.isEmpty(), "Should find results for iPhone 14");
        
        SearchResult exactMatch = results.stream()
            .filter(r -> r.getProduct().getName().equals("iPhone 14"))
            .findFirst()
            .orElse(null);
            
        if (exactMatch != null) {
            assertTrue(exactMatch.getRelevanceScore() > 2.0, 
                      "Exact match should have high relevance score");
            assertTrue(exactMatch.getMatchType().equals("High Relevance") || 
                      exactMatch.getMatchType().equals("Exact Match"), 
                      "Should classify as high relevance or exact match");
        }
    }
} 