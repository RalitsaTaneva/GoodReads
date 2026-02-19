package bg.sofia.uni.fmi.mjt.goodreads.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TextTokenizer {

    private final Set<String> stopwords;

    public TextTokenizer(Reader stopwordsReader) {
        if (stopwordsReader == null) { //to delete it?
            throw new IllegalArgumentException("Reader for stopwords must not be null.");
        }

        try (var br = new BufferedReader(stopwordsReader)) {
            stopwords = br.lines().collect(Collectors.toSet());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not load dataset", ex);
        }
    }

    public List<String> tokenize(String input) {
        if (input == null || input.isBlank()) {
            return List.of();
        }

        return Arrays.stream(input.replaceAll("\\p{Punct}", "")
                .replaceAll("\\s+", " ")
                .toLowerCase()
                .split(" "))                // Split by space into words
            .filter(word -> !word.isBlank())   //?????      // Ignore blank words
            .filter(word -> !stopwords.contains(word)) // Remove stopwords
            .collect(Collectors.toList());
    }

    public Set<String> stopwords() {
        return stopwords;
    }
}