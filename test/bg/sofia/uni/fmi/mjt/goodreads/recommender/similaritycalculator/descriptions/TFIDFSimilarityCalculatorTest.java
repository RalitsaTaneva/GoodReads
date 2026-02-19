package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

public class TFIDFSimilarityCalculatorTest {

    private TFIDFSimilarityCalculator calculator;
    private TextTokenizer mockedTokenizer;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        book1 = mock(Book.class);
        book2 = mock(Book.class);
        mockedTokenizer = mock(TextTokenizer.class);

        when(book1.description()).thenReturn("Science fiction space exploration");
        when(book2.description()).thenReturn("Space and time exploration");

        when(mockedTokenizer.tokenize("Science fiction space exploration"))
            .thenReturn(List.of("science", "fiction", "space", "exploration"));
        when(mockedTokenizer.tokenize("Space and time exploration"))
            .thenReturn(List.of("space", "and", "time", "exploration"));

        Set<Book> books = Set.of(book1, book2);
        calculator = new TFIDFSimilarityCalculator(books, mockedTokenizer);
    }


    @Test
    void testCalculateSimilarityWithNoCommonWords() {
        when(book1.description()).thenReturn("Science fiction and space");
        when(book2.description()).thenReturn("Medieval times and knights");

        double similarity = calculator.calculateSimilarity(book1, book2);

        assertEquals(0.0, similarity,
            "The similarity should be 0.0 because there are no common words between the two descriptions.");
    }

    @Test
    void testCalculateSimilarityWithCommonWords() {
        when(book1.description()).thenReturn("Science fiction space exploration");
        when(book2.description()).thenReturn("Space and time exploration");

        double similarity = calculator.calculateSimilarity(book1, book2);

        assertTrue(similarity > 0.0,
            "The similarity should be greater than 0.0 as there are common words.");
    }

    @Test
    void testCalculateSimilarityWithIdenticalDescriptions() {
        // Arrange
        String description = "Story about FMI students";
        when(book1.description()).thenReturn(description);
        when(book2.description()).thenReturn(description);

        double similarity = calculator.calculateSimilarity(book1, book2);

        assertEquals(1.0, similarity, "The similarity should be 1.0 for identical descriptions.");
    }

    @Test
    void testCalculateSimilarityWithEmptyDescriptionForOneBook() {
        // Arrange
        when(book1.description()).thenReturn("Science fiction space exploration");
        when(book2.description()).thenReturn("");

        // Act
        double similarity = calculator.calculateSimilarity(book1, book2);

        // Assert
        assertEquals(0.0, similarity, "The similarity should be 0.0 because one description is empty.");
    }

    @Test
    void testCalculateSimilarityWithEmptyDescriptions() {
        // Arrange
        when(book1.description()).thenReturn("");
        when(book2.description()).thenReturn("");

        // Act
        double similarity = calculator.calculateSimilarity(book1, book2);

        // Assert
        assertEquals(0.0, similarity, "The similarity should be 0.0 because both descriptions are empty.");
    }

}
