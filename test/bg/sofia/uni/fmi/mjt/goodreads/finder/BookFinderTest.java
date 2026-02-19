package bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookFinderTest {

    private BookFinder bookFinder;
    private TextTokenizer tokenizer;
    private Set<Book> books;

    @BeforeEach
    void setUp() {
        tokenizer = mock(TextTokenizer.class);

        Book book1 = mock(Book.class);
        when(book1.author()).thenReturn("Author One");
        when(book1.genres()).thenReturn(List.of("Science Fiction", "Adventure"));
        when(book1.description()).thenReturn("Space exploration and adventure.");
        when(book1.title()).thenReturn("Exploring the Cosmos");

        Book book2 = mock(Book.class);
        when(book2.author()).thenReturn("Author Two");
        when(book2.genres()).thenReturn(List.of("Fantasy", "Drama"));
        when(book2.description()).thenReturn("Knights and a dragon in a fantasy land.");
        when(book2.title()).thenReturn("Quest of the Dragon");

        books = Set.of(book1, book2);
        bookFinder = new BookFinder(books, tokenizer);
    }

    @Test
    void testConstructorWithNullBooksThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new BookFinder(null, tokenizer),
            "Books set must not be null");
    }

    @Test
    void testConstructorWithEmptyBooksThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new BookFinder(Set.of(), tokenizer),
            "Books set must not be empty");
    }

    @Test
    void testConstructorWithNullTokenizerThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new BookFinder(books, null),
            "Books set must not be null");
    }

    @Test
    void testSearchByAuthorReturnsBooksByAuthor() {
        List<Book> result = bookFinder.searchByAuthor("Author One");
        assertEquals("Author One", result.get(0).author(),
            "The author of the first book in the result list should be 'Author One'.");
    }

    @Test
    void testSearchByAuthorWithInvalidAuthorThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> bookFinder.searchByAuthor(null),
            "Author's name must not be null");
    }

    @Test
    void testSearchByAuthorWithEmptyNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> bookFinder.searchByAuthor(""),
            "Author's name must not be empty");
    }

    @Test
    void testAllGenresReturnsAllUniqueGenres() {
        Set<String> result = bookFinder.allGenres();
        assertEquals(Set.of("Science Fiction", "Adventure", "Fantasy", "Drama"), result,
            "The returned set of genres does not match the expected unique genres.");
    }

//    @Test
//    void testSearchByGenresWithMatchAllReturnsCorrectBooks() {
//        List<Book> result = bookFinder.searchByGenres(Set.of("Science Fiction", "Adventure"), MatchOption.MATCH_ALL);
//        assertEquals("Exploring the Cosmos", result.get(0).title());
//    }

    @Test
    void testSearchByGenresWithMatchAnyReturnsCorrectBooks() {
        List<Book> result = bookFinder.searchByGenres(Set.of("Fantasy", "Adventure"), MatchOption.MATCH_ANY);
        assertEquals(2, result.size(), "All the books matching the genre filter must be added");
    }

    @Test
    void testSearchByGenresWithNullGenresThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> bookFinder.searchByGenres(null, MatchOption.MATCH_ANY),
            "Genres must not be null");
    }

    @Test
    void testSearchByKeywordsWithMatchAllReturnsCorrectBooks() {
        when(tokenizer.tokenize("Space exploration and adventure."))
            .thenReturn(List.of("space", "exploration", "adventure"));
        when(tokenizer.tokenize("Exploring the Cosmos"))
            .thenReturn(List.of("exploring", "cosmos"));

        List<Book> result = bookFinder.searchByKeywords(Set.of("space", "adventure"), MatchOption.MATCH_ALL);
        assertEquals("Author One", result.get(0).author());
    }

    @Test
    void testSearchByKeywordsWithMatchAnyReturnsCorrectBooks() {
        when(tokenizer.tokenize("Knights and a dragon in a fantasy land."))
            .thenReturn(List.of("knights", "dragon", "fantasy"));

        when(tokenizer.tokenize("Quest of the Dragon"))
            .thenReturn(List.of("dragon", "quest"));

        List<Book> result = bookFinder.searchByKeywords(Set.of("dragon", "space"), MatchOption.MATCH_ANY);
        assertEquals(1, result.size());
    }

//    @Test
//    void test2SearchByKeywordsWithMatchAnyReturnsCorrectBooks() {
//        when(tokenizer.tokenize("Knights and a dragon in a fantasy land."))
//            .thenReturn(List.of("knights", "dragon", "fantasy"));
//
//        when(tokenizer.tokenize("Quest of the Dragon"))
//            .thenReturn(List.of("dragon", "quest"));
//
//        List<Book> result = bookFinder.searchByKeywords(Set.of("dragon", "Cosmos"), MatchOption.MATCH_ANY);
//        assertEquals(2, result.size());
//    }

    @Test
    void testSearchByKeywordsWithEmptyKeywordsThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> bookFinder.searchByKeywords(Set.of(), MatchOption.MATCH_ANY));
    }

    @Test
    void testSearchByKeywordsWithNullOptionThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> bookFinder.searchByKeywords(Set.of("space"), null));
    }
}
