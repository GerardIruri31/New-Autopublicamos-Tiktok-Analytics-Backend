package com.example.sbazureappdemo.user.controller;

import com.example.sbazureappdemo.user.dto.UserDTO;
import com.example.sbazureappdemo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping("/module")
    public ResponseEntity<UserDTO> getAppModule(@RequestParam String email) {
        logger.info("Iniciando obtención modulos para usuario " + email +  " ... ");
        return ResponseEntity.ok(userService.moduleByPass(email));
    }

    @GetMapping("/role")
    public ResponseEntity<String> getRole(@RequestParam String email) {
        logger.info("Iniciando obtención role para usuario " + email +  " ... ");
        return ResponseEntity.ok(userService.getRole(email));
    }
}
