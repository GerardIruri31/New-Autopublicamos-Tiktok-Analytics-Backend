package com.example.sbazureappdemo.ordenGeneration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteRequestDTO {
    @NotBlank
    private String correo;
    @NotNull
    private Integer codordentrabajo;
    private String codescena;
    private String tippublicacion;
    private String codlibro;
    private String codcuentatiktok;
    private String codtelefono;
    private Integer codimagenprincipal;
    private Integer codimagenscreenshot;
    private Integer codimagendialogo;
    private Integer codvideo;
    private Integer codsonido;
}
