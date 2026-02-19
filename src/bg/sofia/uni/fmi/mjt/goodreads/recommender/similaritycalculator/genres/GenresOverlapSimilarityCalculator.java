package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.HashSet;
import java.util.Set;

public class GenresOverlapSimilarityCalculator implements SimilarityCalculator {

    @Override
    public double calculateSimilarity(Book first, Book second) {
        if (first == null || second == null || first.genres() == null || second.genres() == null) {
            throw new IllegalArgumentException("Books and their genres must not be null");
        }

        Set<String> firstGenres = new HashSet<>(first.genres());
        Set<String> secondGenres = new HashSet<>(second.genres());

        firstGenres.retainAll(secondGenres);
        int intersectionSize = firstGenres.size();

        int minSize = Math.min(first.genres().size(), second.genres().size());

        if (minSize == 0) {
            return 0.0;
        }

        return (double) intersectionSize / minSize;
    }

}