package com.example.sbazureappdemo.ordenGeneration.dto;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ManualOrderResponseDTO {
    private String codordentrabajo;
    private String codposteador;
    private String codtelefono;
    private String codautora;
    private String codlibro;
    private String codescena;
    private String codcuentatiktok;
    private String codsonido;
    private String desscenahook;
    private String descaption;
    private String destropo;
    private String desslide1keywordshide;
    private String desslide2keywordshide;
    private String deshashtag;
    private String despalote;
    private String codimagenprincipal;
    private String codimagenscreenshot;
    private String codimagendialogo;
    private String codvideo;
    private String desinstrucciones;
    private LocalDate fecplanposteo;
    private String codestadoorden;
    private String tipregistroorden;
    private String flgordenincompleta;
    private String desdatosfaltantesorden;
    private String codusuarioauditoriacreareg;
    private String codusuarioauditoriaactualizareg;
    private LocalDate fecreacionregistro;
    private LocalTime horacreacionregistro;
    private LocalDate fecactualizacionregistro;
    private LocalTime horaactualizacionregistro;
}
