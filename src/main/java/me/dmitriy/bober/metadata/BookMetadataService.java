package me.dmitriy.bober.metadata;


import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class BookMetadataService {

    private final List<BookMetadataExtractor> extractors;

    public BookMetadataService(List<BookMetadataExtractor> extractors) {
        this.extractors = extractors;
    }

    public Optional<ExtractedMetadata> tryExtract(Path file, String extension) {
        return extractors.stream()
                .filter(e -> e.supports(extension))
                .findFirst()
                .map(e -> {
                    try {
                        return e.extract(file);
                    } catch (Exception ex) {
                        return null;
                    }
                });
    }
}
