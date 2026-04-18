package com.andrey.librarymanager.repository;

import com.andrey.librarymanager.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}