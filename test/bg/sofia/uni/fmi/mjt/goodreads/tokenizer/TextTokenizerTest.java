package bg.sofia.uni.fmi.mjt.goodreads.tokenizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TextTokenizerTest {

    private TextTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        String stopwordsData = "the\nand\nis\na\n";
        StringReader stopwordsReader = new StringReader(stopwordsData);
        tokenizer = new TextTokenizer(stopwordsReader);
    }

    @Test
    void testConstructorLoadsStopwordsSuccessfully() {
        Set<String> expectedStopwords = Set.of("the", "and", "is", "a");
        assertEquals(expectedStopwords, tokenizer.stopwords(),
            "Constructor should correctly load stopwords from the reader.");
    }

    @Test
    void testConstructorThrowsExceptionForNullReader() {
        assertThrows(IllegalArgumentException.class, () -> new TextTokenizer(null),
            "Constructor should throw IllegalArgumentException if stopwordsReader is null.");
    }

    @Test
    void testConstructorHandlesEmptyReader() {
        Reader emptyReader = new StringReader(""); // Empty input for stopwords
        TextTokenizer tokenizer = new TextTokenizer(emptyReader);

        assertTrue(tokenizer.stopwords().isEmpty(),
            "Constructor should initialize an empty set of stopwords when the reader is empty.");
    }

    @Test
    void testTokenizeIgnoresStopwords() {
        String input = "The quick brown fox jumps over the lazy dog";
        List<String> tokens = tokenizer.tokenize(input);

        List<String> expectedTokens = List.of("quick", "brown", "fox", "jumps", "over", "lazy", "dog");
        assertEquals(expectedTokens, tokens, "Tokenize should remove stopwords from the input.");
    }

    @Test
    void testTokenizeHandlesPunctuation() {
        String input = "Hello, world! This is a test.";
        List<String> tokens = tokenizer.tokenize(input);

        List<String> expectedTokens = List.of("hello", "world", "this", "test");
        assertEquals(expectedTokens, tokens,
            "Tokenize should remove punctuation and stopwords.");
    }

    @Test
    void testTokenizeHandlesEmptyInput() {
        String input = "";
        List<String> tokens = tokenizer.tokenize(input);

        assertTrue(tokens.isEmpty(), "Tokenize should return an empty list for empty input.");
    }

    @Test
    void testTokenizeHandlesNullInput() {
        String input = null;
        List<String> tokens = tokenizer.tokenize(input);

        assertTrue(tokens.isEmpty(), "Tokenize should return an empty list for null input.");
    }

    @Test
    void testTokenizeRemovesExtraSpaces() {
        String input = "   This   is    a     test   ";
        List<String> tokens = tokenizer.tokenize(input);

        List<String> expectedTokens = List.of("this", "test");
        assertEquals(expectedTokens, tokens,
            "Tokenize should handle and normalize multiple spaces.");
    }
}
