package com.example.sbazureappdemo.ordenGeneration.dto;

import lombok.Data;
import java.util.List;

@Data
public class AuthorRequestDTO {
    private String codposteador;
    private String codtelefono;
    private String tiptelefono;
    private List<String> codcuentatiktok;
}
