package me.dmitriy.bober.service;

import jakarta.transaction.Transactional;
import me.dmitriy.bober.data.UserRepository;
import me.dmitriy.bober.models.User;
import me.dmitriy.bober.models.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void update(User user, int id) {
        User existingUser = userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
        if(user.getFirstName() != null) {
            existingUser.setFirstName(user.getFirstName());
        }
        if(user.getLastName() != null) {
            existingUser.setLastName(user.getLastName());
        }
        if(user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if(user.getBirthDate() != null) {
            existingUser.setBirthDate(user.getBirthDate());
        }
        if(user.getBio() != null) {
            existingUser.setBio(user.getBio());
        }
        if(user.getUserPicturePath() != null) {
            existingUser.setUserPicturePath(user.getUserPicturePath());
        }
    }

    public User getCurrentUser() {
        String nickname = SecurityContextHolder.getContext().getAuthentication().getName();
         return userRepository
                .findByNicknameIgnoreCase(nickname)
                .orElseThrow(() -> new IllegalArgumentException("User with name " + nickname + " not found"));
    }

    public void setBlocked(int id,  boolean blocked) {
        User currentUser = getCurrentUser();
        User target = userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User "+id+" not found"));

        if (currentUser.getId() == target.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нельзя заблокировать самого себя");
        }

        boolean actorIsSudo = UserRole.SUDO.name().equals(currentUser.getRole());
        boolean targetIsStaff = UserRole.ADMIN.name().equals(target.getRole())
                || UserRole.SUDO.name().equals(target.getRole());

        if(!actorIsSudo && targetIsStaff) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Недостаточно прав для изменения статуса этого пользователя");
        }

        target.setBlocked(blocked);
    }

    public void promoteToAdmin(int id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
        user.setRole("ADMIN");
    }

    public void promoteToAuthor(int id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
        user.setRole("AUTHOR");
    }

    public void delete(int id) {
        User currentUser = getCurrentUser();
        if(currentUser.getId() == id) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нельзя удалить самого себя");
        }
        userRepository.deleteById(id);
    }

}
