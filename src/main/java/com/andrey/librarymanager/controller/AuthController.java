package com.andrey.librarymanager.controller;

import com.andrey.librarymanager.dto.LoginRequestDTO;
import com.andrey.librarymanager.dto.LoginResponseDTO;
import com.andrey.librarymanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request){
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }
}
