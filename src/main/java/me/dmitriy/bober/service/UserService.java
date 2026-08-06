package me.dmitriy.bober.service;

import jakarta.transaction.Transactional;
import me.dmitriy.bober.data.UserRepository;
import me.dmitriy.bober.models.User;
import org.springframework.stereotype.Service;

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
    }

}
