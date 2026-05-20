package com.andrey.librarymanager;

import com.andrey.librarymanager.model.User;
import com.andrey.librarymanager.security.JwtService;
import com.andrey.librarymanager.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setup(){
        jwtService = new JwtService();
        jwtService.setSecretKey("wqP1!7JewbY$$M%glT2^U%Vc9v04@I0H");
    }

    @Test
    void shouldGenerateTokenSucessfully(){
        User user = User.builder()
                .id(1L)
                .name("Andrey")
                .email("andrey@gmail.com")
                .phone("0000000000")
                .password("senha secreta")
                .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        //act
        String result = jwtService.generateToken(userDetails);

        //assert
        assertNotNull(result);
        assertEquals("andrey@gmail.com", jwtService.extractUsername(result));
    }
}
