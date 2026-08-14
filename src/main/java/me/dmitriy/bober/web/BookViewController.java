package me.dmitriy.bober.web;

import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.models.Author;
import me.dmitriy.bober.models.Book;
import me.dmitriy.bober.models.User;
import me.dmitriy.bober.service.UserService;
import me.dmitriy.bober.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.io.Resource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Controller
@RequestMapping("/books")
public class BookViewController {

    private final BookRepository bookRepository;
    private final FileStorageService fileStorageService;
    private final UserService userService;
    private final AuthorRepository authorRepository;

    public BookViewController(BookRepository bookRepository, FileStorageService fileStorageService, UserService userService, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.fileStorageService = fileStorageService;
        this.userService = userService;
        this.authorRepository = authorRepository;
    }

    @GetMapping("/{id}")
    public String getBook(Model model, @PathVariable int id) {
        Book book = bookRepository.findByIdWithAuthors(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        User currentUser = userService.getCurrentUser();
        Author author = authorRepository.findByUserId(currentUser.getId()).orElse(null);
        model.addAttribute("book", book);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("author", author);
        return "book-page";
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadBook(@PathVariable int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        Resource fileResource = fileStorageService.loadAsResource(book.getFilePath());

        if(!fileResource.exists() || !fileResource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }

        String downloadFilename = book.getTitle() + "." + book.getFileFormat();
        String encodedFilename = URLEncoder.encode(downloadFilename, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(fileResource);
    }
}
