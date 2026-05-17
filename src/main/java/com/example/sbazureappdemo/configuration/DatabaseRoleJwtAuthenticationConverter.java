package com.example.sbazureappdemo.configuration;

import com.example.sbazureappdemo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class DatabaseRoleJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String correo = resolveEmail(jwt);
        List<GrantedAuthority> authorities = new ArrayList<>();

        String tiprol = null;
        if (correo != null && !correo.isBlank()) {
            tiprol = userRepository.findTiprolByCodusuario(correo);
        }
        if (tiprol != null && !tiprol.isBlank()) {
            String rolNormalizado = tiprol.trim().toLowerCase(Locale.ROOT);
            switch (rolNormalizado) {
                case "adm" -> authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                case "sup" -> authorities.add(new SimpleGrantedAuthority("ROLE_SUP"));
                case "pa" -> authorities.add(new SimpleGrantedAuthority("ROLE_PA"));
                case "aut" -> authorities.add(new SimpleGrantedAuthority("ROLE_AUT"));
                default -> authorities.add(new SimpleGrantedAuthority("ROLE_DEFAULT"));
            }
        }
        return new JwtAuthenticationToken(jwt, authorities, correo);
    }

    private String resolveEmail(Jwt jwt) {
        Object emails = jwt.getClaims().get("emails");
        if (emails instanceof List<?> list && !list.isEmpty()) {
            Object first = list.get(0);
            if (first != null) {
                String s = first.toString();
                if (!s.isBlank()) {
                    return s;
                }
            }
        }
        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) {
            return email;
        }
        String preferred = jwt.getClaimAsString("preferred_username");
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return null;
    }
}