package com.example.sbazureappdemo.user.service;

import com.example.sbazureappdemo.user.controller.UserController;
import com.example.sbazureappdemo.user.dto.UserDTO;
import com.example.sbazureappdemo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    public UserDTO moduleByPass (String email) {
        UserDTO modules = userRepository.getAllModules(email);
        if (modules.getModules().isEmpty()) {
            throw new IllegalArgumentException("No hay modulos que hagan match con user " + email);
        }
        return modules;
    }

    public String getRole (String email) {
        String role = userRepository.findTiprolByCodusuario(email);
        if (role == null) {
            throw new IllegalArgumentException("Usuario no tiene role: " + email);
        }
        return role;
    }
}

