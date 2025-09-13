package com.example.userservice.security;

import com.example.userservice.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;

    public TokenAuthenticationFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            authService.getUserForToken(token).ifPresent(user -> {
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority(user.getRole().name())
                );
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getEmail(),null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            });
        }
    filterChain.doFilter(request,response);
    }
}
