package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.composite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;


import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

class CompositeSimilarityCalculatorTest {

    private CompositeSimilarityCalculator compositeCalculator;

    private SimilarityCalculator calculator1;
    private SimilarityCalculator calculator2;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        calculator1 = mock(SimilarityCalculator.class);
        calculator2 = mock(SimilarityCalculator.class);

        book1 = mock(Book.class);
        book2 = mock(Book.class);
    }

    @Test
    void testConstructorWithNullMapThrowsExceptionType() {
        assertThrows(IllegalArgumentException.class, () -> new CompositeSimilarityCalculator(null),
            "Map cannot be null");
    }

    @Test
    void testConstructorWithEmptyMapThrowsExceptionType() {
        assertThrows(IllegalArgumentException.class, () -> new CompositeSimilarityCalculator(Map.of()),
            "Map cannot be empty");
    }

    @Test
    void testCalculateSimilarityWithNullFirstBookThrowsExceptionType() {
        compositeCalculator = new CompositeSimilarityCalculator(Map.of(calculator1, 0.5));

        assertThrows(IllegalArgumentException.class, () -> compositeCalculator.calculateSimilarity(null, book2),
            "First book cannot be null");
    }

    @Test
    void testCalculateSimilarityWithNullSecondBookThrowsExceptionType() {
        compositeCalculator = new CompositeSimilarityCalculator(Map.of(calculator1, 0.5));

        assertThrows(IllegalArgumentException.class, () -> compositeCalculator.calculateSimilarity(book1, null),
            "Second book cannot be null");
    }

    @Test
    void testCalculateSimilarityWithValidInputs() {
        when(calculator1.calculateSimilarity(book1, book2)).thenReturn(0.8);
        when(calculator2.calculateSimilarity(book1, book2)).thenReturn(0.6);

        compositeCalculator = new CompositeSimilarityCalculator(Map.of(calculator1, 0.5, calculator2, 0.5));

        double similarity = compositeCalculator.calculateSimilarity(book1, book2);

        assertEquals(0.7, similarity, 1e-9,
            "The similarity should be the weighted sum of individual similarities.");
    }

    @Test
    void testCalculateSimilarityWithDifferentWeights() {
        when(calculator1.calculateSimilarity(book1, book2)).thenReturn(0.9);
        when(calculator2.calculateSimilarity(book1, book2)).thenReturn(0.4);

        compositeCalculator = new CompositeSimilarityCalculator(Map.of(calculator1, 0.7, calculator2, 0.3));

        double similarity = compositeCalculator.calculateSimilarity(book1, book2);

        assertEquals(0.75, similarity, 1e-9,
            "The similarity should consider the weights correctly.");
    }

    @Test
    void testCalculateSimilarityWithOneCalculator() {
        when(calculator1.calculateSimilarity(book1, book2)).thenReturn(0.9);

        compositeCalculator = new CompositeSimilarityCalculator(Map.of(calculator1, 1.0));

        double similarity = compositeCalculator.calculateSimilarity(book1, book2);

        assertEquals(0.9, similarity, 1e-9,
            "The similarity should equal the single calculator's result.");
    }

    @Test
    void testCalculateSimilarityWithZeroWeights() {
        when(calculator1.calculateSimilarity(book1, book2)).thenReturn(0.9);
        when(calculator2.calculateSimilarity(book1, book2)).thenReturn(0.4);

        compositeCalculator = new CompositeSimilarityCalculator(Map.of(calculator1, 0.0, calculator2, 0.0));

        double similarity = compositeCalculator.calculateSimilarity(book1, book2);

        assertEquals(0.0, similarity, 1e-9, "The similarity should be 0.0 if all weights are 0.");
    }
}

