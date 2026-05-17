package com.example.sbazureappdemo.selectAuthorCodes.service;

import com.example.sbazureappdemo.selectAuthorCodes.repository.AuthorCodesRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthorCodesService {
    Logger logger = LoggerFactory.getLogger(AuthorCodesService.class);
    private final AuthorCodesRepository authorCodesRepository;

    public List<Map<String, Object>> getCodautorasByCorreo(String correo) {
        logger.info("Buscando codautora por correo: {}", correo);
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'correo' es obligatorio.");
        }
        return authorCodesRepository.findCodautorasByCorreo(correo);
    }

    public List<Map<String, Object>> getLibrosByCorreo(String correo) {
        logger.info("Buscando codlibro por correo: {}", correo);
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'correo' es obligatorio");
        }
        return authorCodesRepository.findLibrosByCorreo(correo);
    }

    public List<Map<String, Object>> getSeudonimosLibros(String correo) {
        logger.info("Buscando codautora y codlibro por correo: {}", correo);
        if (correo == null || correo.trim().isEmpty()) {
            throw new IllegalArgumentException("El parámetro 'correo' es obligatorio.");
        }
        return authorCodesRepository.findSeudonimoLibro(correo);
    }
}
