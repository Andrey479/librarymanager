package com.andrey.librarymanager.service;

import com.andrey.librarymanager.dto.UserRequestDTO;
import com.andrey.librarymanager.dto.UserResponseDTO;
import com.andrey.librarymanager.model.User;
import com.andrey.librarymanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO register(UserRequestDTO request) {
        return toResponse(userRepository.save(toEntity(request)));
    }

    public List<UserResponseDTO> listAll(){
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private User toEntity(UserRequestDTO request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
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
