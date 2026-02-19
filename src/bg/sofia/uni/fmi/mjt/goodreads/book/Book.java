package bg.sofia.uni.fmi.mjt.goodreads.book;

import java.util.Arrays;
import java.util.List;

public record Book(
    String ID,
    String title,
    String author,
    String description,
    List<String> genres,
    double rating,
    int ratingCount,
    String URL
) {

    public static Book of(String[] tokens) {
        validateTokens(tokens);

        String id = tokens[0].trim();
        String title = tokens[1].trim();
        String author = tokens[2].trim();
        String description = tokens[3].trim();
        List<String> genres = parseGenres(tokens[4]);
        double rating = parseDouble(tokens[5], "rating");
        int ratingCount = parseInt(tokens[6], "rating count");
        String url = tokens[7].trim();

        return new Book(id, title, author, description, genres, rating, ratingCount, url);
    }

    private static void validateTokens(String[] tokens) {
        if (tokens == null || tokens.length != 8) {
            throw new IllegalArgumentException("Invalid tokens array. Expected 8 elements.");
        }
    }

//    private static List<String> parseGenres(String genresString) {
//        if (genresString.startsWith("[") && genresString.endsWith("]")) {
//            genresString = genresString.substring(1, genresString.length() - 1);
//        }
//        return Arrays.stream(genresString.split(","))
//            .map(String::trim)
//            .toList();
//    }

    private static List<String> parseGenres(String genresString) {
        if (genresString == null || genresString.isBlank()) {
            return List.of();
        }

        if (genresString.startsWith("[") && genresString.endsWith("]")) {
            genresString = genresString.substring(1, genresString.length() - 1);
        }

        return Arrays.stream(genresString.split(","))
            .map(String::trim)
            .filter(genre -> !genre.isEmpty())
            .toList();
    }

    private static double parseDouble(String token, String fieldName) {
        try {
            return Double.parseDouble(token.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format for " + fieldName + ": " + token, e);
        }
    }

    private static int parseInt(String token, String fieldName) {
        try {
            return Integer.parseInt(token.replace(",", "").trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format for " + fieldName + ": " + token, e);
        }
    }
}