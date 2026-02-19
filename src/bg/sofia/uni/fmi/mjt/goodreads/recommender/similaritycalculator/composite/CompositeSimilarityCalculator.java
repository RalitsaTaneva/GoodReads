package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.composite;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.Map;

public class CompositeSimilarityCalculator implements SimilarityCalculator {

    private final Map<SimilarityCalculator, Double> similarityCalculatorMap;

    public CompositeSimilarityCalculator(Map<SimilarityCalculator, Double> similarityCalculatorMap) {
        if (similarityCalculatorMap == null || similarityCalculatorMap.isEmpty()) {
            throw new IllegalArgumentException("Invalid similarity calculator map");
        }
        this.similarityCalculatorMap = Map.copyOf(similarityCalculatorMap);
    }

    @Override
    public double calculateSimilarity(Book first, Book second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Books cannot be null.");
        }

        return similarityCalculatorMap.entrySet()
            .stream()
            .mapToDouble(entry -> {
                SimilarityCalculator calculator = entry.getKey();
                double weight = entry.getValue();

                return calculator.calculateSimilarity(first, second) * weight;
            })
            .sum();
    }
}