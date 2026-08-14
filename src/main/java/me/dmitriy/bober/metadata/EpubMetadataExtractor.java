package me.dmitriy.bober.metadata;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class EpubMetadataExtractor implements BookMetadataExtractor {

    @Override
    public boolean supports(String extension) {
        return "epub".equalsIgnoreCase(extension);
    }

    @Override
    public ExtractedMetadata extract(Path filepath) {
        try (InputStream is = Files.newInputStream(filepath)) {
            Book book = new EpubReader().readEpub(is);

            String title = extractTitle(book);
            String author = extractAuthor(book);
            String publisher = extractPublisher(book);
            byte[] cover = extractCover(book);

            return new ExtractedMetadata(title, author, publisher, cover);
        } catch (Exception e) {
            throw new IllegalStateException("Epub file parsing failed", e);
        }
    }

    private String extractTitle(Book book) {
        if (book.getTitle() != null && !book.getTitle().isBlank()) {
            return book.getTitle().trim();
        }
        return null;
    }

    private String extractAuthor(Book book) {
        List<Author> authors = book.getMetadata().getAuthors();
        if (authors == null || authors.isEmpty()) {
            return null;
        }

        String joined = authors.stream()
                .map(author -> Stream.of(author.getFirstname(), author.getLastname())
                        .filter(Objects::nonNull)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.joining(" ")))
                .filter(name -> !name.isBlank())
                .collect(Collectors.joining(", "));

        return joined.isBlank() ? null : joined;
    }

    private String extractPublisher(Book book) {
        List<String> publishers = book.getMetadata().getPublishers();
        if (publishers != null && !publishers.isEmpty()) {
            String pub = publishers.get(0);
            return (pub != null && !pub.isBlank()) ? pub.trim() : null;
        }
        return null;
    }

    private byte[] extractCover(Book book) {
        try {
            if (book.getCoverImage() != null) {
                return book.getCoverImage().getData();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}