package me.dmitriy.bober.service;

import jakarta.transaction.Transactional;
import me.dmitriy.bober.data.AuthorRepository;
import me.dmitriy.bober.data.AuthorRequestRepository;
import me.dmitriy.bober.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class AuthorRequestService {

    private final AuthorRequestRepository authorRequestRepository;
    private final AuthorRepository authorRepository;
    private final UserService userService;

    public AuthorRequestService(AuthorRequestRepository authorRequestRepository,
                                AuthorRepository authorRepository, UserService userService) {
        this.authorRequestRepository = authorRequestRepository;
        this.authorRepository = authorRepository;
        this.userService = userService;
    }

    public void submit(String resume){
        User currentUser = userService.getCurrentUser();
        boolean alreadyStaff = UserRole.AUTHOR.name().equals(currentUser.getRole())
                || UserRole.ADMIN.name().equals(currentUser.getRole())
                || UserRole.SUDO.name().equals(currentUser.getRole());
        if(alreadyStaff){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Вы уже автор (или выше в стаффе)");
        }
        authorRequestRepository.findByUserIdAndStatus(currentUser.getId(), AuthorRequestStatus.PENDING)
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "У вас уже есть активная заявка!");
                });

        AuthorRequest request = new AuthorRequest();
        request.setUser(currentUser);
        request.setResume(resume);
        authorRequestRepository.save(request);
    }

    List<AuthorRequest> findPending(){
        return authorRequestRepository.findByStatus(AuthorRequestStatus.PENDING);
    }

    public void approve(int requestId){
        AuthorRequest request = authorRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка "+requestId+" не найдена!"));
        if(request.getStatus() != AuthorRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Заявка уже обработана");
        }

        User user = request.getUser();
        Author author = new Author();

        author.setFirstName(user.getFirstName());
        author.setLastName(user.getLastName());
        author.setBirthDate(user.getBirthDate());
        author.setBio(user.getBio());
        author.setUser(user);

        authorRepository.save(author);
        user.setRole(UserRole.AUTHOR.name());
        request.setStatus(AuthorRequestStatus.APPROVED);
    }

    public void reject(int requestId){
        AuthorRequest request = authorRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request "+requestId+" not found!"));

        if(request.getStatus() != AuthorRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Заявка уже обработана!");
        }
        request.setStatus(AuthorRequestStatus.REJECTED);
    }
}
