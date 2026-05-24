package com.andrey.librarymanager.service;

import com.andrey.librarymanager.dto.UserRequestDTO;
import com.andrey.librarymanager.dto.UserResponseDTO;
import com.andrey.librarymanager.model.User;
import com.andrey.librarymanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO register(UserRequestDTO request) {
        log.info("User successfully registered.");
        return toResponse(userRepository.save(toEntity(request)));
    }

    public List<UserResponseDTO> listAll(){
        log.info("All users listed correctly.");
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private User toEntity(UserRequestDTO request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return user;
    }

    private UserResponseDTO toResponse (User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setName(user.getName());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setPhone(user.getPhone());
        return userResponseDTO;
    }
}
