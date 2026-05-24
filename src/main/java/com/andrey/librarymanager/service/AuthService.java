package com.andrey.librarymanager.service;

import com.andrey.librarymanager.dto.LoginRequestDTO;
import com.andrey.librarymanager.dto.LoginResponseDTO;
import com.andrey.librarymanager.security.JwtService;
import com.andrey.librarymanager.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO request){
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);
        return new LoginResponseDTO(token);
    }
}
