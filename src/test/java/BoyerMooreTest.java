import algorithm.BoyerMoore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Тесты для алгоритма Бойера-Мура

public class BoyerMooreTest {

    @Test
    public void testBasicSearch() {
        String text = "hello world";
        String pattern = "world";
        int result = BoyerMoore.search(text, pattern);
        assertEquals(6, result);
    }

    @Test
    public void testPatternNotFound() {
        String text = "hello world";
        String pattern = "java";
        int result = BoyerMoore.search(text, pattern);
        assertEquals(-1, result);
    }

    @Test
    public void testPatternAtBeginning() {
        String text = "hello world";
        String pattern = "hello";
        int result = BoyerMoore.search(text, pattern);
        assertEquals(0, result);
    }

    @Test
    public void testEmptyPattern() {
        String text = "hello world";
        String pattern = "";
        int result = BoyerMoore.search(text, pattern);
        assertEquals(0, result);
    }

    @Test
    public void testEmptyText() {
        String text = "";
        String pattern = "hello";
        int result = BoyerMoore.search(text, pattern);
        assertEquals(-1, result);
    }

    @Test
    public void testPatternLongerThanText() {
        String text = "hi";
        String pattern = "hello";
        int result = BoyerMoore.search(text, pattern);
        assertEquals(-1, result);
    }

    @Test
    public void testSingleCharacter() {
        String text = "abcdef";
        String pattern = "c";
        int result = BoyerMoore.search(text, pattern);
        assertEquals(2, result);
    }
}

