package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TFIDFSimilarityCalculator implements SimilarityCalculator {

    private Set<Book> books;
    private TextTokenizer tokenizer;

    public TFIDFSimilarityCalculator(Set<Book> books, TextTokenizer tokenizer) {
        this.books = books;
        this.tokenizer = tokenizer;
    }

    @Override
    public double calculateSimilarity(Book first, Book second) {
        Map<String, Double> tfIdfScoresFirst = computeTFIDF(first);
        Map<String, Double> tfIdfScoresSecond = computeTFIDF(second);

        return cosineSimilarity(tfIdfScoresFirst, tfIdfScoresSecond);
    }

    public Map<String, Double> computeTFIDF(Book book) {
        Map<String, Double> tf = computeTF(book);
        Map<String, Double> idf = computeIDF(book);

        if (tf.isEmpty() || idf.isEmpty()) {
            return Map.of();  // return empty map for empty descriptions or no valid tokens
        }

        return tf.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue() * idf.getOrDefault(entry.getKey(), 0.0)));
    }

    public Map<String, Double> computeTF(Book book) {
        List<String> tokens = getValidTokens(book);
        if (tokens.isEmpty()) {
            return Map.of();
        }

        Map<String, Long> wordOccurrences = tokens.stream()
            .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

        double totalWords = tokens.size();

        return wordOccurrences.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue() / totalWords));
    }

    public Map<String, Double> computeIDF(Book book) {
        List<String> tokens = getValidTokens(book);

        if (tokens.isEmpty()) {
            return Map.of();
        }

        Set<String> uniqueWords = new HashSet<>(tokens);
        int totalBooks = books.size();

        Map<String, Long> documentFrequencies = computeDocumentFrequencies(uniqueWords, books);

        return documentFrequencies.entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> Math.log(
                    (double) totalBooks / (1 + entry.getValue())))); // use the smooth method???
    }

    private Map<String, Long> computeDocumentFrequencies(Set<String> uniqueWords, Set<Book> books) {
        if (uniqueWords.isEmpty()) {
            return Map.of();
        }

        return uniqueWords.stream()
            .collect(Collectors.toMap(
                word -> word,
                word -> books.stream()
                    .filter(book -> isWordInBookDescription(book, word))
                    .count()));
    }

    private boolean isWordInBookDescription(Book book, String word) {
        List<String> tokens = getValidTokens(book);
        return tokens.contains(word);
    }

    private List<String> getValidTokens(Book book) {
        String description = book.description();
        if (description == null || description.isBlank()) {
            return List.of();
        }

        return tokenizer.tokenize(description);
    }

    private double cosineSimilarity(Map<String, Double> first, Map<String, Double> second) {
        double magnitudeFirst = magnitude(first.values());
        double magnitudeSecond = magnitude(second.values());

        if (magnitudeFirst == 0 || magnitudeSecond == 0) {
            return 0.0;
        }

        return dotProduct(first, second) / (magnitudeFirst * magnitudeSecond);
    }

    private double dotProduct(Map<String, Double> first, Map<String, Double> second) {
        Set<String> commonKeys = new HashSet<>(first.keySet());
        commonKeys.retainAll(second.keySet());

        return commonKeys.stream()
            .mapToDouble(word -> first.get(word) * second.get(word))
            .sum();
    }

    private double magnitude(Collection<Double> input) {
        double squaredMagnitude = input.stream()
            .map(v -> v * v)
            .reduce(0.0, Double::sum);

        return Math.sqrt(squaredMagnitude);
    }
}