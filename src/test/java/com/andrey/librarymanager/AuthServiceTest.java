package com.andrey.librarymanager;

import com.andrey.librarymanager.dto.LoginRequestDTO;
import com.andrey.librarymanager.dto.LoginResponseDTO;
import com.andrey.librarymanager.security.JwtService;
import com.andrey.librarymanager.security.UserDetailsServiceImpl;
import com.andrey.librarymanager.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsServiceImpl userDetailsService;
    @Mock private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoginSuccessfully(){
        LoginRequestDTO request = new LoginRequestDTO(
                "emailaleatorio@gmail.com",
                "senha qualquer"
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(token);

        //act
        LoginResponseDTO response = authService.login(request);

        //assert
        assertNotNull(response);
        assertEquals(token, response.getToken());
    }

    @Test
    void shouldNotLoginSucessfully(){
        LoginRequestDTO request = new LoginRequestDTO(
                "emailaleatorio@gmail.com",
                "senha qualquer"
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new UsernameNotFoundException("Deu erro como esperado"));

        //act + asset
        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));
    }

}
