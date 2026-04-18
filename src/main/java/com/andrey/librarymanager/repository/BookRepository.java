package com.andrey.librarymanager.repository;

import com.andrey.librarymanager.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}