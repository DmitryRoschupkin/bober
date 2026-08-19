package me.dmitriy.bober.service;

import jakarta.transaction.Transactional;
import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.models.Author;
import me.dmitriy.bober.models.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void coauthorToAuthorConnect(int authorId, String coauthor){
        Author author = authorRepository.findById(authorId).orElse(null);
        if(author == null){
            return;
        }
        List<Book> coauthorsBooks = bookRepository.findBooksWithCertainCoauthor(coauthor);

        for(Book book : coauthorsBooks){
            if (!book.getAuthors().contains(author)){
                book.getAuthors().add(author);
            }
            String currentCoauthors = book.getCoauthors();
            if(currentCoauthors != null && !currentCoauthors.isBlank()){
                String updated = currentCoauthors.replace(coauthor, "");
                updated = updated.replaceAll(",\\s*,", ",")
                        .replaceAll("^\\s*,\\s*", "")
                        .replaceAll("\\s*,\\s*$", "")
                        .trim();
                book.setCoauthors(updated.isEmpty() ? null : updated);
            }
            bookRepository.save(book);
        }
    }

}
