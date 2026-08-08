package com.andrey.librarymanager;

import com.andrey.librarymanager.security.JwtAuthenticationFilter;
import com.andrey.librarymanager.security.JwtService;
import com.andrey.librarymanager.security.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {
    @Mock private JwtService jwtService;
    @Mock private UserDetailsServiceImpl userDetailsService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private void invokeDoFilterInternal() throws Exception {
        Method method = JwtAuthenticationFilter.class.getDeclaredMethod(
                "doFilterInternal", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);
        method.invoke(filter, request, response, filterChain);
    }

    @Test
    void shouldNotPropagateExceptionWhenTokenIsExpired() throws Exception {
        // given
        when(request.getHeader("Authorization")).thenReturn("Bearer token-expirado");
        when(jwtService.extractUsername("token-expirado"))
                .thenThrow(mock(ExpiredJwtException.class));

        // act + assert: não pode propagar a exceção para fora do filtro
        assertDoesNotThrow(this::invokeDoFilterInternal);

        // a requisição deve seguir a cadeia mesmo sem autenticação válida
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotPropagateExceptionWhenUserFromTokenIsNotFound() throws Exception {
        // given
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido-mas-usuario-sumiu");
        when(jwtService.extractUsername("token-valido-mas-usuario-sumiu")).thenReturn("fantasma@email.com");
        when(userDetailsService.loadUserByUsername("fantasma@email.com"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // act + assert
        assertDoesNotThrow(this::invokeDoFilterInternal);

        verify(filterChain).doFilter(request, response);
    }

}
