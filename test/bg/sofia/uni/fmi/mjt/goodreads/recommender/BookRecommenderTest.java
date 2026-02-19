package bg.sofia.uni.fmi.mjt.goodreads.recommender;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

public class BookRecommenderTest {

    private BookRecommender recommender;
    private SimilarityCalculator mockCalculator;
    private Set<Book> books;

    private Book book1;
    private Book book2;
    private Book book3;


    @BeforeEach
    void setUp() {
        book1 = mock(Book.class);
        book2 = mock(Book.class);
        book3 = mock(Book.class);

        books = Set.of(book1, book2, book3);

        mockCalculator = mock(SimilarityCalculator.class);
        recommender = new BookRecommender(books, mockCalculator);
    }

    @Test
    void testConstructorWithNullBooks() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
            () -> new BookRecommender(null, mockCalculator),
            "Constructor should throw an exception when books set is null.");
    }

    @Test
    void testConstructorThrowsWhenBooksIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BookRecommender(null, mockCalculator),
            "Constructor should throw an exception when books set is null.");
    }

    @Test
    void testConstructorThrowsWhenBooksSetIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new BookRecommender(Set.of(), mockCalculator),
            "Constructor should throw an exception when books set is empty.");
    }

    @Test
    void testConstructorThrowsWhenCalculatorIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BookRecommender(books, null),
            "Constructor should throw an exception when SimilarityCalculator is null.");
    }

    @Test
    void testRecommendBooksThrowsWhenOriginBookIsNull() {
        assertThrows(IllegalArgumentException.class, () -> recommender.recommendBooks(null, 2),
            "RecommendBooks should throw an exception when the origin book is null.");
    }

    @Test
    void testRecommendBooksThrowsWhenMaxNIsNonPositive() {
        assertThrows(IllegalArgumentException.class, () -> recommender.recommendBooks(book1, 0),
            "RecommendBooks should throw an exception when maxN is non-positive.");
    }

    @Test
    void testRecommendBooksCalculatesCorrectNumberOfRecommendations() {
        when(mockCalculator.calculateSimilarity(book1, book2)).thenReturn(0.9);
        when(mockCalculator.calculateSimilarity(book1, book3)).thenReturn(0.8);

        SortedMap<Book, Double> recommendations = recommender.recommendBooks(book1, 2);

        assertEquals(2, recommendations.size(), "Should return two recommended books.");
    }

    @Test
    void testRecommendBooksCalculatesCorrectScoreForBook2() {
        when(mockCalculator.calculateSimilarity(book1, book2)).thenReturn(0.9);

        SortedMap<Book, Double> recommendations = recommender.recommendBooks(book1, 2);

        assertEquals(0.9, recommendations.get(book2),
            "Book2 should have the correct similarity score.");
    }

    @Test
    void testRecommendBooksDoesNotIncludeOriginBook() {
        when(mockCalculator.calculateSimilarity(book1, book2)).thenReturn(0.9);
        when(mockCalculator.calculateSimilarity(book1, book3)).thenReturn(0.8);

        SortedMap<Book, Double> recommendations = recommender.recommendBooks(book1, 2);

        assertFalse(recommendations.containsKey(book1), "Recommendations should not include the origin book.");
    }

    @Test
    void testRecommendBooksLimitsResultsToMaxN() {
        when(mockCalculator.calculateSimilarity(book1, book2)).thenReturn(0.9);
        when(mockCalculator.calculateSimilarity(book1, book3)).thenReturn(0.8);

        SortedMap<Book, Double> recommendations = recommender.recommendBooks(book1, 1);

        assertEquals(1, recommendations.size(), "Should return only one recommended book.");
    }

    @Test
    void testRecommendBooksSortsTopRecommendationAsBook3() {
        when(mockCalculator.calculateSimilarity(book1, book2)).thenReturn(0.7);
        when(mockCalculator.calculateSimilarity(book1, book3)).thenReturn(0.9);

        SortedMap<Book, Double> recommendations = recommender.recommendBooks(book1, 2);

        List<Book> recommendedBooks = new ArrayList<>(recommendations.keySet());

        assertEquals(book3, recommendedBooks.get(0),
            "the book with the highest similarity score should be the top recommendation.");
    }
}
