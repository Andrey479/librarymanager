package com.andrey.librarymanager.controller;

import com.andrey.librarymanager.dto.UserRequestDTO;
import com.andrey.librarymanager.dto.UserResponseDTO;
import com.andrey.librarymanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO userRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(userRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listAll(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.listAll());
    }
}
