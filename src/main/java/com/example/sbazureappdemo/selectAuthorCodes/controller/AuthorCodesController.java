package com.example.sbazureappdemo.selectAuthorCodes.controller;

import com.example.sbazureappdemo.selectAuthorCodes.service.AuthorCodesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/autoras")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthorCodesController {
    private final AuthorCodesService authorCodesService;

    @GetMapping("/codautora")
    public ResponseEntity<Map<String,Object>> getCodautorasByCorreo(@RequestParam("correo") String correo) {
        List<Map<String, Object>> codautoras = authorCodesService.getCodautorasByCorreo(correo.trim());
        return ResponseEntity.ok(Map.of("codautoras", codautoras));
    }

    @GetMapping("/libros")
    public ResponseEntity<Map<String,Object>> getLibrosByCorreo(@RequestParam("correo") String correo) {
        List<Map<String, Object>> libros = authorCodesService.getLibrosByCorreo(correo.trim());
        return ResponseEntity.ok(Map.of("codlibros", libros));
    }

    @GetMapping("/dbqueries")
    public ResponseEntity<Map<String,Object>> getSeudo_Libros(@RequestParam("correo") String correo) {
        List<Map<String, Object>> libros = authorCodesService.getSeudonimosLibros(correo.trim());
        return ResponseEntity.ok(Map.of("seudo_libro", libros));
    }
}
