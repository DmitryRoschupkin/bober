package me.dmitriy.bober.service;



import org.springframework.transaction.annotation.Transactional;
import me.dmitriy.bober.data.BookRepository;
import me.dmitriy.bober.data.MarkRepository;
import me.dmitriy.bober.data.UserRepository;
import me.dmitriy.bober.models.Book;
import me.dmitriy.bober.models.Mark;
import me.dmitriy.bober.models.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {

    private final MarkRepository markRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    public BookService(BookRepository bookRepository, MarkRepository markRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.markRepository = markRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void toggleMark(int bookId, int userId, String type) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
        Optional<Mark> existing = markRepository.findByUserIdAndBookId(userId, bookId);
        if (existing.isPresent()) {
            Mark mark = existing.get();
            if(mark.getMark().equals(type)) {
                decrementCount(book, type);
                markRepository.delete(mark);
            } else  {
                decrementCount(book, mark.getMark());
                incrementCount(book, type);
                mark.setMark(type.toUpperCase());
                markRepository.save(mark);
            }
        } else  {
            Mark mark = new Mark();

            User user = userRepository.getReferenceById(userId);

            mark.setBook(book);
            mark.setUser(user);
            mark.setMark(type);

            incrementCount(book, type);
            markRepository.save(mark);
        }
    }

    private void incrementCount(Book book, String type) {
        if("LIKE".equalsIgnoreCase(type)) {
            book.setLikesCount(book.getLikesCount() + 1);
        } else if("DISLIKE".equalsIgnoreCase(type)) {
            book.setDislikesCount(book.getDislikesCount() + 1);
        }
    }

    private void decrementCount(Book book, String type) {
        if("LIKE".equals(type)) {
            book.setLikesCount(Math.max(0, book.getLikesCount() - 1));
        } else if("DISLIKE".equalsIgnoreCase(type)) {
            book.setDislikesCount(Math.max(0, book.getLikesCount() - 1));
        }
    }

}
