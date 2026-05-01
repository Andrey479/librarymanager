package com.andrey.librarymanager;

import com.andrey.librarymanager.dto.UserRequestDTO;
import com.andrey.librarymanager.model.User;
import com.andrey.librarymanager.repository.UserRepository;
import com.andrey.librarymanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully() {
        //given
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "user",
                "email@email.com",
                "55 77777-7777"
        );
        User user = new User();

        //when
        when(userRepository.save(any(User.class))).thenReturn(user);

        //then
        assertNotNull(userService.register(userRequestDTO));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldListAllUsers(){
        when(userRepository.findAll()).thenReturn(List.of());
        assertNotNull(userService.listAll());
        verify(userRepository).findAll();
    }
}
