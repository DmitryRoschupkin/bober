package me.dmitriy.bober.web;


import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.metadata.BookMetadataExtractor;
import me.dmitriy.bober.metadata.BookMetadataService;
import me.dmitriy.bober.metadata.ExtractedMetadata;
import me.dmitriy.bober.models.Author;
import me.dmitriy.bober.models.Book;
import me.dmitriy.bober.models.User;
import me.dmitriy.bober.service.UserService;
import me.dmitriy.bober.storage.FileStorageService;
import me.dmitriy.bober.storage.StoredFile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.expression.Sets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/authors/books")
@PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN', 'SUDO')")
public class AuthorBookUploadController {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "fb2", "epub", "txt");

    private final FileStorageService fileStorageService;
    private final BookMetadataService bookMetadataService;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final UserService userService;

    public AuthorBookUploadController(FileStorageService fileStorageService, BookMetadataService bookMetadataService, AuthorRepository authorRepository, BookRepository bookRepository, UserService userService) {
        this.fileStorageService = fileStorageService;
        this.bookMetadataService = bookMetadataService;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    @GetMapping("/upload")
    public String showUploadForm() {
        return "author/upload-book";
    }

    @PostMapping("/upload")
    public String uploadBook(@RequestParam MultipartFile bookFile,
                             @RequestParam(required = false) MultipartFile coverFile,
                             @RequestParam(required = false) String title,
                             @RequestParam(required = false) String publisher,
                             @RequestParam(required = false) String genre,
                             @RequestParam(required = false) Integer year,
                             @RequestParam(required = false) String description) throws IOException {
        String extension = extensionOf(bookFile.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Расширение не поддерживается: "+extension);
        }

        User currentUser = userService.getCurrentUser();
        Author author = authorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "У вас нет авторского профиля"));

        StoredFile storedBook = fileStorageService.store(bookFile, "books");

        ExtractedMetadata metadata = bookMetadataService
                .tryExtract(fileStorageService.resolve(storedBook.storedPath()), extension)
                .orElse(new ExtractedMetadata(null, null, null, null));

        String resolvedTitle = firstNotBlank(title, metadata.title());
        String resolvedPublisher = firstNotBlank(publisher, metadata.publisher());

        if(resolvedTitle == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не удалось определить название из метаданных - " +
                    "введите вручную (либо файл в формате без них, например .txt");
        }
        String coverPath = null;
        if(coverFile != null && !coverFile.isEmpty()) {
            coverPath = fileStorageService.store(coverFile, "covers").storedPath();
        } else if (metadata.coverImage() != null) {
            coverPath = fileStorageService.storeBytes(metadata.coverImage(), "covers", "jpg");
        }

        Book book = new Book();
        book.setTitle(resolvedTitle);
        book.setPublisher(resolvedPublisher);
        book.setGenre(genre);
        book.setYear(year);
        book.setCoverPath(coverPath);
        book.setFilePath(storedBook.storedPath());
        book.setFileFormat(extension);
        book.setCreatedAt(LocalDateTime.now());
        book.setAuthors(new ArrayList<>(List.of(author)));
        book.setDescription(description);

        bookRepository.save(book);

        return "redirect:/account";
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private String firstNotBlank(String manual, String extracted) {
        if(manual != null && !manual.isBlank()) return manual.trim();
        if(extracted != null && !extracted.isBlank()) return extracted.trim();
        return null;
    }
}
