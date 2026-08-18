package me.dmitriy.bober.web;

import me.dmitriy.bober.data.UserRepository;
import me.dmitriy.bober.models.User;
import me.dmitriy.bober.service.UserService;
import me.dmitriy.bober.storage.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/account")
public class AccountEditController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    public AccountEditController(UserRepository userRepository, UserService userService, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/edit/{id}")
    public String editAccountForm(Model model,
                                  @PathVariable int id) {
        User currentUser = userService.getCurrentUser();
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
        List<String> fieldNames = user.fieldNames();
        model.addAttribute("user", user);
        model.addAttribute("fieldNames", fieldNames);
        model.addAttribute("currentUser", currentUser);
        return "account-edit";
    }

    @PostMapping("/edit/{id}")
    public String editAccountProcess(@PathVariable int id,
                                     @ModelAttribute User user,
                                     @RequestParam(required = false) MultipartFile userPictureFile,
                                     @RequestParam(value = "removePicture", defaultValue = "false") boolean removePicture,
                                     @RequestParam(required = false) String currentPassword,
                                     @RequestParam(required = false) String newPassword,
                                     @RequestParam(required = false) String confirmPassword) throws IOException {

        User existingUser = userRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setBirthDate(user.getBirthDate());
        existingUser.setBio(user.getBio());
        String currentPath = existingUser.getUserPicturePath();
        if (removePicture) {
            if (currentPath != null && !currentPath.isBlank()) {
                fileStorageService.delete(currentPath);
            }
            user.setUserPicturePath(null);
        } else if (userPictureFile != null &&  !userPictureFile.isEmpty()) {
            if (currentPath != null && !currentPath.isBlank()) {
                fileStorageService.delete(currentPath);
            }
            String newUserPicturePath = fileStorageService.store(userPictureFile, "userPictures").storedPath();
            user.setUserPicturePath(newUserPicturePath);
        } else {
            user.setUserPicturePath(currentPath);
        }

        if (newPassword != null && !newPassword.isBlank()) {
            boolean isPasswordChanged = userService.changePassword(existingUser, currentPassword, newPassword, confirmPassword);
            if (!isPasswordChanged) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка при смене пароля. Проверьте форму смены пароля");
            }
        }

        userService.update(existingUser, id);
        return "redirect:/account/"+id;
    }
}
