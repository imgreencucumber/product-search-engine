import index.InvertedIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// Тесты для инвертированного индекса

public class InvertedIndexTest {
    private InvertedIndex index;

    @BeforeEach
    public void setUp() {
        index = new InvertedIndex();
    }

    @Test
    public void testAddDocument() {
        index.addDocument("apple iphone smartphone", 1);
        index.addDocument("samsung galaxy android", 2);

        Set<Integer> appleResults = index.search("apple");
        assertEquals(1, appleResults.size());
        assertTrue(appleResults.contains(1));

        Set<Integer> samsungResults = index.search("samsung");
        assertEquals(1, samsungResults.size());
        assertTrue(samsungResults.contains(2));
    }

    @Test
    public void testSearchMultipleWords() {
        index.addDocument("apple iphone smartphone", 1);
        index.addDocument("apple macbook laptop", 2);
        index.addDocument("samsung galaxy smartphone", 3);

        Set<Integer> appleResults = index.search("apple");
        assertEquals(2, appleResults.size());
        assertTrue(appleResults.contains(1));
        assertTrue(appleResults.contains(2));

        Set<Integer> smartphoneResults = index.search("smartphone");
        assertEquals(2, smartphoneResults.size());
        assertTrue(smartphoneResults.contains(1));
        assertTrue(smartphoneResults.contains(3));

        Set<Integer> appleSmartphoneResults = index.search("apple smartphone");
        assertEquals(1, appleSmartphoneResults.size());
        assertTrue(appleSmartphoneResults.contains(1));
    }

    @Test
    public void testSearchNonExistentWord() {
        index.addDocument("apple iphone smartphone", 1);
        
        Set<Integer> results = index.search("nonexistent");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testEmptyQuery() {
        index.addDocument("apple iphone smartphone", 1);
        
        Set<Integer> results = index.search("");
        assertTrue(results.isEmpty());
    }
}

