package bg.sofia.uni.fmi.mjt.goodreads.recommender;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BookRecommender implements BookRecommenderAPI {

    private final Set<Book> initialBooks;
    private final SimilarityCalculator calculator;

    public BookRecommender(Set<Book> initialBooks, SimilarityCalculator calculator) {
        if (initialBooks == null || initialBooks.isEmpty()) {
            throw new IllegalArgumentException("Books set must not be null or empty.");
        }
        if (calculator == null) {
            throw new IllegalArgumentException("SimilarityCalculator must not be null.");
        }
        this.initialBooks = Set.copyOf(initialBooks);
        this.calculator = calculator;
    }

    @Override
    public SortedMap<Book, Double> recommendBooks(Book origin, int maxN) {
        if (origin == null) {
            throw new IllegalArgumentException("Origin book must not be null.");
        }
        if (maxN <= 0) {
            throw new IllegalArgumentException("maxN must be greater than 0.");
        }

        Map<Book, Double> similarityScores = calculateSimilarityScores(origin);
        return getTopNSimilarBooks(similarityScores, maxN);
    }

    private Map<Book, Double> calculateSimilarityScores(Book originBook) {
        return initialBooks.stream()
            .filter(book -> !book.equals(originBook))
            .collect(Collectors.toMap(
                book -> book,
                book -> calculator.calculateSimilarity(originBook, book)));
    }

    private SortedMap<Book, Double> getTopNSimilarBooks(Map<Book, Double> similarityScores, int maxN) {
        return similarityScores.entrySet()
            .stream()
            .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())) // Sort by value descending
            .limit(maxN)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1, // potential duplicates
                () -> new TreeMap<>(
                    Comparator.comparingDouble(key -> similarityScores.getOrDefault(key, Double.MIN_VALUE))
                        .reversed())));
    }
}