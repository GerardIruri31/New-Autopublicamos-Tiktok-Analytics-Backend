package com.example.sbazureappdemo.authorGraphs.dto;

import lombok.Data;

@Data
public class EfectividadAutorMetaDTO {
    private String codautora;     // Código de la autora (ej. "JE", "NJ")
    private String nbautora;      // Nombre de la autora (ej. "Jade R.", "N.J.")
    private String codmes;        // Mes en formato 'Mon-YY' (ej. "Apr-25")
    private Integer numposts;
}