package com.example.taskmanager.security;

import com.example.taskmanager.entity.Organization;
import com.example.taskmanager.entity.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 🔥 1. Skip auth endpoints (VERY IMPORTANT)
        String path = request.getRequestURI();
        if (path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        try {
            // 🔥 2. If no token → continue (Spring will handle auth)
            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.substring(7);

            // 🔥 3. Validate token
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT Token");
                return;
            }

            Claims claims = jwtUtil.getClaims(token);

            Object userIdObj = claims.get("userId");
            Object roleObj = claims.get("role");
            Object orgIdObj = claims.get("orgId");

            if (userIdObj == null || roleObj == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid Token Data");
                return;
            }

            // 🔥 4. Build user object
            User user = new User();
            user.setId(((Number) userIdObj).longValue());
            user.setRole((String) roleObj);

            if (orgIdObj != null) {
                Organization org = new Organization();
                org.setId(((Number) orgIdObj).longValue());
                user.setOrganization(org);
            }

            // 🔥 5. Attach user to request (your usage)
            request.setAttribute("user", user);

            // 🔥 6. Set authentication (CRITICAL FIX)
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

            SecurityContextHolder.clearContext();
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}