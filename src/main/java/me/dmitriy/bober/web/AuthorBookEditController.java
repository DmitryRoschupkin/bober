package me.dmitriy.bober.web;

import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.metadata.BookMetadataService;
import me.dmitriy.bober.models.Author;
import me.dmitriy.bober.models.Book;
import me.dmitriy.bober.models.User;
import me.dmitriy.bober.service.UserService;
import me.dmitriy.bober.storage.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Set;

@Controller
@RequestMapping("/authors/books")
@PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN', 'SUDO')")
public class AuthorBookEditController {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "fb2", "epub", "txt");

    private final FileStorageService fileStorageService;
    private final BookMetadataService bookMetadataService;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final UserService userService;

    public AuthorBookEditController(FileStorageService fileStorageService, BookMetadataService bookMetadataService, AuthorRepository authorRepository, BookRepository bookRepository, UserService userService) {
        this.fileStorageService = fileStorageService;
        this.bookMetadataService = bookMetadataService;
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        User currentUser = userService.getCurrentUser();
        Author author = authorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не автор этой книги!"));
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Книга не найдена"));

        if(!book.getAuthors().contains(author)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "У вас нет прав редактировать чужую книгу!");
        }
        model.addAttribute("book", book);
        return "author/edit-book";
    }

    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable int id,
                             @RequestParam(required = false) MultipartFile coverFile,
                             @RequestParam String title,
                             @RequestParam(required = false) String publisher,
                             @RequestParam(required = false) String genre,
                             @RequestParam(required = false) Integer year,
                             @RequestParam(required = false) String description) throws IOException {
        User currentUser = userService.getCurrentUser();
        Author author = authorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "У вас нет авторского профиля! Вы не Достоевский!"));
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Книга не найдена, увы и ах"));

        if(!book.getAuthors().contains(author)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "У вас нет прав редактировать чужую книгу!");
        }
        if(title == null || title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Название книги не может быть пустым");
        }
        if (coverFile != null &&  !coverFile.isEmpty()) {
            if (book.getCoverPath() != null) {
                fileStorageService.delete(book.getCoverPath());
            }
            String newCoverPath = fileStorageService.store(coverFile, "covers").storedPath();
            book.setCoverPath(newCoverPath);
        }

        book.setTitle(title.trim());
        book.setGenre(genre);
        book.setPublisher(publisher != null ? publisher.trim() : null);
        book.setYear(year);
        book.setDescription(description);

        bookRepository.save(book);

        return "redirect:/books/" + id;
    }
}
