package com.example.sbazureappdemo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;


import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${security.jwt.expected-issuer}")
    private String expectedIssuer;

    @Value("${security.jwt.expected-policy-tfp}")
    private String expectedPolicyTfp;

    @Value("${security.jwt.expected-api-audience}")
    private String expectedApiAudience;

    @Value("${security.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${app.cors.allowed-origins}")
    private String allowedOriginsCsv;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MdcFilter mdcFilter, DatabaseRoleJwtAuthenticationConverter databaseRoleJwtAuthenticationConverter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults()) // ✅ IMPORTANTE
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ✅ preflight CORS
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(
                                "/apifycall/filtrar",
                                "/apifycall/excel/download",
                                "/datamaintenance/uploadexcel",
                                "/datamaintenance/tablerecords",
                                "/datamaintenance/download",
                                "/pagraphs/getdata",
                                "/apifycall/excel/read-tiktok-accounts",
                                "/databasequery/filter"
                        ).hasRole("ADMIN")
                        .requestMatchers("/orden/filter/**","/order/queries/**","/order/auto","/order/edit","order/manual","order/delete").hasAnyRole("SUP", "ADMIN","PA")
                        .anyRequest().hasAnyRole("PA", "SUP", "ADMIN","AUT")
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.decoder(jwtDecoder())
                                .jwtAuthenticationConverter(databaseRoleJwtAuthenticationConverter)
                        )
                );
        http.addFilterAfter(mdcFilter, BearerTokenAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    JwtDecoder jwtDecoder() {
        String jwk = (jwkSetUri == null) ? "" : jwkSetUri.trim();
        String issuer = (expectedIssuer == null) ? "" : expectedIssuer.trim();
        String audienceExpected = (expectedApiAudience == null) ? "" : expectedApiAudience.trim();
        String tfpExpected = (expectedPolicyTfp == null) ? "" : expectedPolicyTfp.trim();

        if (jwk.isBlank()) throw new IllegalStateException("security.jwt.jwk-set-uri is required");
        if (issuer.isBlank()) throw new IllegalStateException("security.jwt.expected-issuer is required");
        if (audienceExpected.isBlank()) throw new IllegalStateException("security.jwt.expected-api-audience is required");
        if (tfpExpected.isBlank()) throw new IllegalStateException("security.jwt.expected-policy-tfp is required");


        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwk).build();
        OAuth2TokenValidator<Jwt> issuerValidator =
                JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> {
            boolean ok = jwt.getAudience() != null && jwt.getAudience().contains(audienceExpected);
            return ok
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid audience", null));
        };
        OAuth2TokenValidator<Jwt> tfpValidator = jwt -> {
            String tfp = jwt.getClaimAsString("tfp");
            boolean ok = tfp != null && tfpExpected.equalsIgnoreCase(tfp);
            return ok ? OAuth2TokenValidatorResult.success() : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid policy (tfp)", null));
        };
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                issuerValidator, audienceValidator, tfpValidator
        ));
        return decoder;
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> allowedOrigins = Arrays.stream((allowedOriginsCsv == null ? "" : allowedOriginsCsv).split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
        config.setAllowedOrigins(allowedOrigins);

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type","Accept"));
        config.setExposedHeaders(List.of("Content-Disposition"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
