package me.dmitriy.bober.data;

import me.dmitriy.bober.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {

}
