package com.andrey.librarymanager.service;

import com.andrey.librarymanager.dto.LoginRequestDTO;
import com.andrey.librarymanager.dto.LoginResponseDTO;
import com.andrey.librarymanager.security.JwtService;
import com.andrey.librarymanager.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO request){
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            log.info("Authentication successful for user: {}", request.getEmail());

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String token = jwtService.generateToken(userDetails);

            log.info("Token generated successfully.");

            return new LoginResponseDTO(token);
        } catch (AuthenticationException e) {
            log.warn("Error authenticating user {} where the error is: {}", request.getEmail(), e);
            throw new UsernameNotFoundException(e.toString());
        }
    }
}
