package me.dmitriy.bober.metadata;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;

@Component
public class PdfMetadataExtractor implements BookMetadataExtractor {

    @Override
    public boolean supports(String extension) {
        return "pdf".equalsIgnoreCase(extension);
    }

    @Override
    public ExtractedMetadata extract(Path filepath) {
        try (PDDocument document = Loader.loadPDF(filepath.toFile())) {

            PDDocumentInformation info = document.getDocumentInformation();

            String title = (info != null && info.getTitle() != null && !info.getTitle().isBlank())
                    ? info.getTitle().trim()
                    : null;

            String author = (info != null && info.getAuthor() != null && !info.getAuthor().isBlank())
                    ? info.getAuthor().trim()
                    : null;

            String publisher = (info != null && info.getProducer() != null && !info.getProducer().isBlank())
                    ? info.getProducer().trim()
                    : null;

            byte[] cover = extractCover(document);

            return new ExtractedMetadata(title, author, publisher, cover);

        } catch (Exception e) {
            throw new IllegalStateException("PDF file parsing failed", e);
        }
    }

    private byte[] extractCover(PDDocument document) {
        if (document.getNumberOfPages() == 0) {
            return null;
        }

        try {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 150);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(bufferedImage, "png", baos);
                return baos.toByteArray();
            }
        } catch (IOException e) {
            return null;
        }
    }
}