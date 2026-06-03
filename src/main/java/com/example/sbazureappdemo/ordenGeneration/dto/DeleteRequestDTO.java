package com.example.sbazureappdemo.ordenGeneration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteRequestDTO {
    @NotBlank
    private String correo;
    @NotNull
    private Long codordentrabajo;
    @NotBlank
    private String codescena;
    @NotBlank
    private String tippublicacion;
    @NotBlank
    private String codlibro;
    @NotBlank
    private String codcuentatiktok;
    @NotBlank
    private String codtelefono;
    @NotNull
    private Integer codimagenprincipal;
    @NotNull
    private Integer codimagenscreenshot;
    @NotNull
    private Integer codimagendialogo;
    @NotNull
    private Integer codvideo;
    @NotNull
    private Integer codsonido;
}
