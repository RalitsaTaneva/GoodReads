package bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookFinder implements BookFinderAPI {

    private final Set<Book> books;
    private final TextTokenizer tokenizer;

    public BookFinder(Set<Book> books, TextTokenizer tokenizer) {
        if (books == null || books.isEmpty()) {
            throw new IllegalArgumentException("Books set must not be null or empty.");
        }
        if (tokenizer == null) {
            throw new IllegalArgumentException("TextTokenizer must not be null.");
        }
        this.books = books;
        this.tokenizer = tokenizer;
    }

    public Set<Book> allBooks() {
        return Collections.unmodifiableSet(books);
    }

    @Override
    public List<Book> searchByAuthor(String authorName) {
        if (authorName == null || authorName.isEmpty()) {
            throw new IllegalArgumentException("Invalid author");
        }

        return books.stream()
            .filter(b -> b.author().equalsIgnoreCase(authorName)) //ignoreCase?
            .toList();
    }

    @Override
    public Set<String> allGenres() {
        return books.stream()
            .flatMap(book -> book.genres().stream())
            .collect(Collectors.toSet());
    }

    @Override
    public List<Book> searchByGenres(Set<String> genres, MatchOption option) {
        if (genres == null) {
            throw new IllegalArgumentException("Genres must not be null.");
        }

        return books.stream()
            .filter(book -> isMatchingGenres(book, genres, option))
            .collect(Collectors.toList());
    }

    private boolean isMatchingGenres(Book book, Set<String> genres, MatchOption option) {
        if (book.genres() == null || book.genres().isEmpty()) {
            return false;
        }

        Set<String> bookGenres = book.genres().stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet());

        Set<String> lowercaseGenres = genres.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet());

        if (lowercaseGenres.isEmpty()) {
            return false;
        }

        switch (option) {
            case MATCH_ALL:
                return bookGenres.containsAll(genres);
            case MATCH_ANY:
                return lowercaseGenres.stream()
                    .map(String::toLowerCase)
                    .anyMatch(bookGenres::contains); //  !book.genres().isEmpty() ?
            default:
                throw new IllegalArgumentException("Invalid match option");
        }
    }

    @Override
    public List<Book> searchByKeywords(Set<String> keywords, MatchOption option) {
        if (keywords == null || keywords.isEmpty()) {
            throw new IllegalArgumentException("Keywords must not be null or empty.");
        }
        if (option == null) {
            throw new IllegalArgumentException("MatchOption must not be null.");
        }

        Set<String> lowerCaseKeywords = keywords.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toSet());

        return books.stream()
            .filter(book -> matchesKeywords(book, lowerCaseKeywords, option))
            .collect(Collectors.toList());
    }

    private boolean matchesKeywords(Book book, Set<String> keywords, MatchOption option) {
        Set<String> descriptionTokens = new HashSet<>(tokenizer.tokenize(book.description()));
        Set<String> titleTokens = new HashSet<>(tokenizer.tokenize(book.title()));

        Set<String> combinedTokens = new HashSet<>(descriptionTokens);
        combinedTokens.addAll(titleTokens);

        //System.out.println("Combined tokens for book: " + combinedTokens); //for debug
        switch (option) {
            case MATCH_ALL:
                return keywords.stream().allMatch(combinedTokens::contains);
            case MATCH_ANY:
                return keywords.stream().anyMatch(combinedTokens::contains);
            default:
                throw new IllegalArgumentException("Invalid MatchOption.");
        }
    }
}