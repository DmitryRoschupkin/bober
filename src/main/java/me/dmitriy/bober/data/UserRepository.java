package me.dmitriy.bober.data;


import me.dmitriy.bober.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
