package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

public class GenresOverlapSimilarityCalculatorTest {

    private GenresOverlapSimilarityCalculator calculator;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        calculator = new GenresOverlapSimilarityCalculator();

        book1 = mock(Book.class);
        book2 = mock(Book.class);
    }

    @Test
    void testCalculateSimilarityWithNullFirstBook() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateSimilarity(null, book2),
            "Method should throw IllegalArgumentException when the first book is null.");
    }

    @Test
    void testCalculateSimilarityWithNullSecondBook() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateSimilarity(book1, null),
            "Method should throw IllegalArgumentException when the second book is null.");
    }

    @Test
    void testCalculateSimilarityWithNullGenresInFirstBook() {
        when(book1.genres()).thenReturn(null);
        when(book2.genres()).thenReturn(List.of("Fantasy"));

        assertThrows(IllegalArgumentException.class,
            () -> calculator.calculateSimilarity(book1, book2),
            "Method should throw IllegalArgumentException when the genres of the first book are null.");
    }

    @Test
    void testCalculateSimilarityWithNullGenresInSecondBook() {
        when(book1.genres()).thenReturn(List.of("Fantasy"));
        when(book2.genres()).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
            () -> calculator.calculateSimilarity(book1, book2),
            "Method should throw IllegalArgumentException when the genres of the second book are null.");
    }

    @Test
    void testCalculateSimilarityWithIdenticalGenres() {
        when(book1.genres()).thenReturn(List.of("Fantasy", "Adventure"));
        when(book2.genres()).thenReturn(List.of("Fantasy", "Adventure"));

        double similarity = calculator.calculateSimilarity(book1, book2);

        assertEquals(1.0, similarity, "Similarity should be 1.0 when genres are identical.");
    }

    @Test
    void testCalculateSimilarityWithPartialOverlap() {
        when(book1.genres()).thenReturn(List.of("Fantasy", "Adventure"));
        when(book2.genres()).thenReturn(List.of("Fantasy", "Science Fiction"));

        double similarity = calculator.calculateSimilarity(book1, book2);

        assertEquals(0.5, similarity, "Similarity should be 0.5 for partial overlap.");
    }


    @Test
    void testCalculateSimilarityWithNoOverlap() {
        when(book1.genres()).thenReturn(List.of("Fantasy", "Adventure"));
        when(book2.genres()).thenReturn(List.of("Science Fiction", "Horror"));

        double similarity = calculator.calculateSimilarity(book1, book2);

        assertEquals(0.0, similarity, "Similarity should be 0.0 when there is no overlap.");
    }

    @Test
    void testCalculateSimilarityWithOneBookHavingNoGenres() {
        when(book1.genres()).thenReturn(List.of());
        when(book2.genres()).thenReturn(List.of("Fantasy", "Adventure"));

        double similarity = calculator.calculateSimilarity(book1, book2);

        assertEquals(0.0, similarity, "Similarity should be 0.0 when one book has no genres.");
    }


    @Test
    void testCalculateSimilarityWithBothBooksHavingNoGenres() {
        when(book1.genres()).thenReturn(List.of());
        when(book2.genres()).thenReturn(List.of());

        double similarity = calculator.calculateSimilarity(book1, book2);

        assertEquals(0.0, similarity, "Similarity should be 0.0 when both books have no genres.");
    }

}