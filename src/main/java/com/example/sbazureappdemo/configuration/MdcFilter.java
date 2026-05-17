
package com.example.sbazureappdemo.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class MdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String sessionId = firstNonBlank(request.getHeader("X-Request-Id"), request.getHeader("X-Correlation-Id"));
            if (sessionId == null) {
                sessionId = UUID.randomUUID().toString();
            }
            MDC.put("sessionId", sessionId);
            String userId = resolveEmailFromSecurityContext();
            MDC.put("userId", userId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private static String resolveEmailFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String emailFromEmails = extractFirstEmailFromB2C(jwt);
            if (emailFromEmails != null) return emailFromEmails;
            String email = jwt.getClaimAsString("email");
            if (email != null && !email.isBlank()) return email;
            String preferred = jwt.getClaimAsString("preferred_username");
            if (preferred != null && !preferred.isBlank()) return preferred;
        }
        return "anonymous";
    }

    private static String extractFirstEmailFromB2C(Jwt jwt) {
        Object emails = jwt.getClaims().get("emails");
        if (emails instanceof List<?> list && !list.isEmpty()) {
            Object first = list.get(0);
            if (first != null) {
                String s = first.toString();
                return s.isBlank() ? null : s;
            }
        }
        return null;
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
