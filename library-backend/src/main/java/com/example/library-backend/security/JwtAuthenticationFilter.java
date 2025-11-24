package com.example.library-backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization"; private static final String BEARER_PREFIX = "Bearer ";
    @Autowired private JwtUtil jwtUtil; @Autowired private UserDetailsService userDetailsService;

    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try { String jwt = getJwtFromRequest(request); if (!StringUtils.hasText(jwt)) { filterChain.doFilter(request, response); return; }
            String username = jwtUtil.extractUsername(jwt); if (!StringUtils.hasText(username)) { filterChain.doFilter(request, response); return; }
            if (SecurityContextHolder.getContext().getAuthentication() == null) { authenticateUser(request, jwt, username); }
        } catch (Exception e) { logger.error("Error processing JWT authentication for request: {}", request.getRequestURI(), e); } filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) { String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) return bearerToken.substring(BEARER_PREFIX.length()); return null; }

    private void authenticateUser(HttpServletRequest request, String jwt, String username) {
        try { if (!jwtUtil.validateToken(jwt)) { logger.warn("Invalid JWT token for user: {}", username); return; }
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); if (userDetails == null || !userDetails.isEnabled()) return;
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) { logger.error("Error during user authentication for: {}", username, e); }
    }

    @Override protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException { String path = request.getRequestURI(); return path.startsWith("/api/auth/") || path.equals("/error"); }
}