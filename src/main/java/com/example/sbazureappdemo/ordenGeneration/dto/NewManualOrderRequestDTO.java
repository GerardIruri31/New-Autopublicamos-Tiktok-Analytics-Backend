package com.example.sbazureappdemo.ordenGeneration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;


@Data
public class NewManualOrderRequestDTO {
    @NotBlank
    private String correo;
    @NotBlank(message = "Publisher PA is required")
    private String codposteador;
    private String codtelefono;
    @NotBlank(message = "Author Name is required")
    private String codautora;
    @NotBlank(message = "Book Name is required")
    private String codlibro;
    @NotBlank(message = "Post Type is required")
    private String tippublicacion;
    private String codescena;
    private String codcuentatiktok;
    @NotNull(message = "Sound Code is required")
    private Integer codsonido;
    @NotBlank(message = "Scene Hook is required")
    private String desscenahook;
    private String descaption;
    private String destropo;
    private String desslide1keywordshide;
    private String desslide2keywordshide;
    private String deshashtag;
    @NotBlank(message = "Full Stick is required")
    private String despalote;
    private String codimagenprincipal;
    private String codimagenscreenshot;
    private String codimagendialogo;
    private String codvideo;
    private String desinstrucciones;
    private LocalDate  fecplanposteo;
    private Integer codestadoorden;



}