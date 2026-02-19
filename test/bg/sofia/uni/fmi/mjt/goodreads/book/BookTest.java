package bg.sofia.uni.fmi.mjt.goodreads.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookTest {

    private String[] validTokens;

    @BeforeEach
    void setUp() {
        validTokens = new String[] {
            "1",
            "The Hobbit",
            "J.R.R. Tolkien",
            "A fantasy novel",
            "[Fantasy, Adventure]",
            "4.8",
            "1500",
            "http://example.com"
        };
    }

    @Test
    void testValidBookCreationId() {
        Book book = Book.of(validTokens);

        assertEquals("1", book.ID());
    }

    @Test
    void testValidBookCreationTitle() {
        Book book = Book.of(validTokens);

        assertEquals("The Hobbit", book.title());
    }

    @Test
    void testValidBookCreationAuthor() {
        Book book = Book.of(validTokens);

        assertEquals("J.R.R. Tolkien", book.author());
    }

    @Test
    void testValidBookCreationDescription() {
        Book book = Book.of(validTokens);

        assertEquals("A fantasy novel", book.description());
    }

    @Test
    void testValidBookCreationGenres() {
        Book book = Book.of(validTokens);

        assertEquals(List.of("Fantasy", "Adventure"), book.genres());
    }

    @Test
    void testValidBookCreationRating() {
        Book book = Book.of(validTokens);

        assertEquals(4.8, book.rating());
    }

    @Test
    void testValidBookCreationRatingCount() {
        Book book = Book.of(validTokens);

        assertEquals(1500, book.ratingCount());
    }

    @Test
    void testValidBookCreationUrl() {
        Book book = Book.of(validTokens);

        assertEquals("http://example.com", book.URL());
    }

    @Test
    void testEmptyGenres() {
        validTokens[4] = "";

        Book book = Book.of(validTokens);

        assertIterableEquals(List.of(), book.genres());
    }

    @Test
    void testBlankGenres() {
        validTokens[4] = " ";

        Book book = Book.of(validTokens);

        assertIterableEquals(List.of(), book.genres());
    }

    @Test
    void testInvalidTokensLengthThrowsException() {
        String[] invalidTokens = {"1", "The Hobbit", "J.R.R. Tolkien", "A fantasy novel", "[Fantasy, Adventure]"};

        assertThrows(IllegalArgumentException.class, () -> Book.of(invalidTokens),
            "Invalid tokens array. Expected 8 elements.");
    }

    @Test
    void testInvalidRatingFormatThrowsException() {
        validTokens[5] = "Nanana"; // the rating field

        assertThrows(IllegalArgumentException.class, () -> Book.of(validTokens),
            "Invalid format for rating");
    }

    @Test
    void testInvalidRatingCountFormatThrowsException() {
        validTokens[6] = "Nanana"; //rating count field

        assertThrows(IllegalArgumentException.class, () -> Book.of(validTokens),
            "Invalid format for rating count");
    }

    @Test
    void testValidRatingCountWithCommas() {
        validTokens[6] = "5,691,311"; // Example with commas

        assertDoesNotThrow(() -> Book.of(validTokens));
    }
}
