package com.example.sbazureappdemo.ordenGeneration.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class FiltersRequestDTO {
    private String correo;
    private String codposteador;


    private String tiptelefono;

    private String codtelefono;
    private List<String> codcuentatiktok;
    private String codautora;
    private String codlibro;
    private String tippublicacion;
    private String codescena;

    private String flgprioridadescena;
    private String flgprioridasonido;
    private String flgprioridadimagenvideo;


    private LocalDate fecinicioplanposteo;
    private LocalDate fecfinplanposteo;

    private Integer ctdordenesmetaauto;
    private Integer ctdordenesmetamanual;

}
