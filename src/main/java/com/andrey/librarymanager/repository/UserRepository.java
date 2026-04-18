package com.andrey.librarymanager.repository;

import com.andrey.librarymanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}