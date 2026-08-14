package me.dmitriy.bober.metadata;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface BookMetadataExtractor {

    boolean supports(String extension);

    ExtractedMetadata extract(Path filepath);
}
