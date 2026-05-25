package com.andrey.librarymanager;

import com.andrey.librarymanager.dto.UserRequestDTO;
import com.andrey.librarymanager.dto.UserResponseDTO;
import com.andrey.librarymanager.model.User;
import com.andrey.librarymanager.repository.UserRepository;
import com.andrey.librarymanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully() {
        //given
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "user",
                "email@email.com",
                "55 77777-7777",
                "senhaCriptografada"
        );
        User user = new User();

        //when
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("senhaCriptografada");

        //then
        assertNotNull(userService.register(userRequestDTO));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldListAllUsers(){
        //arrange
        List<User> users = new ArrayList<>();
        users.add(User.builder().id(5L).build());

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(
                users,
                pageable,
                users.size()
        );

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        //act
        Page<UserResponseDTO> response = userService.listAll(pageable);

        //assert
        assertEquals(5L, response.getContent().getFirst().getId());
    }
}
