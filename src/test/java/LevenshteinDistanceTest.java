import algorithm.LevenshteinDistance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LevenshteinDistanceTest {

    @Test
    public void testIdenticalStrings() {
        int distance = LevenshteinDistance.calculate("hello", "hello");
        assertEquals(0, distance);
    }

    @Test
    public void testCompletelyDifferentStrings() {
        int distance = LevenshteinDistance.calculate("abc", "xyz");
        assertEquals(3, distance);
    }

    @Test
    public void testSingleInsertion() {
        int distance = LevenshteinDistance.calculate("cat", "cats");
        assertEquals(1, distance);
    }

    @Test
    public void testSingleDeletion() {
        int distance = LevenshteinDistance.calculate("cats", "cat");
        assertEquals(1, distance);
    }

    @Test
    public void testSingleSubstitution() {
        int distance = LevenshteinDistance.calculate("cat", "bat");
        assertEquals(1, distance);
    }

    @Test
    public void testEmptyStrings() {
        int distance = LevenshteinDistance.calculate("", "");
        assertEquals(0, distance);
    }

    @Test
    public void testOneEmptyString() {
        int distance = LevenshteinDistance.calculate("hello", "");
        assertEquals(5, distance);
        
        distance = LevenshteinDistance.calculate("", "world");
        assertEquals(5, distance);
    }

    @Test
    public void testCaseInsensitive() {
        int distance = LevenshteinDistance.calculate("Hello", "hello");
        assertEquals(0, distance);
    }

    @Test
    public void testComplexExample() {
        int distance = LevenshteinDistance.calculate("kitten", "sitting");
        assertEquals(3, distance); // k->s, e->i, insert g
    }
}

