package com.juan.app_estacionamiento_tandil.config;

import com.juan.app_estacionamiento_tandil.services.JwtService;
import com.juan.app_estacionamiento_tandil.services.UserDetailsServiceImp;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImp userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImp userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        System.out.println("\n--- [DEBUG] JWT Filter Start ---");
        System.out.println("Path: " + request.getServletPath());
        System.out.println("Auth Header received: " + authHeader);

        // 1. Check if the header exists and has the correct format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Result: No Authorization header or doesn't start with 'Bearer '. Skipping filter.");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Extract the token and handle potential JS issues
            jwt = authHeader.substring(7);

            // Check for common frontend bugs (sending "undefined" or "null" as string)
            if (jwt.equalsIgnoreCase("undefined") || jwt.equalsIgnoreCase("null") || jwt.isEmpty()) {
                System.err.println("ERROR: Received invalid string '" + jwt + "' instead of a real JWT.");
                filterChain.doFilter(request, response);
                return;
            }

            username = jwtService.extractUsername(jwt);
            System.out.println("Token extracted. Subject (Username): " + username);

            // 3. Authenticate if user is found and no session exists yet
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 4. Validate token against Database
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Final authentication step
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("SUCCESS: User '" + username + "' authenticated.");
                } else {
                    System.out.println("WARNING: Token is not valid for user '" + username + "'.");
                }
            }
        } catch (Exception e) {
            // Catch MalformedJwtException, ExpiredJwtException, etc.
            System.err.println("CRITICAL ERROR PROCESSING JWT: " + e.getMessage());
        }

        System.out.println("--- [DEBUG] JWT Filter End ---\n");
        filterChain.doFilter(request, response);
    }
}