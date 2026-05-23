package com.example.sbazureappdemo.orderQueries.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Data
public class FiltersRequest {
    private String correo;
    private String codposteador;
    private String codtelefono;
    private String codautora;
    private String codlibro;
    private String codescena;
    private Integer codsonido;
    private String codcuentatiktok;
    private String codusuarioauditoriacreareg;
    private Integer codestadoorden;
    private String flgordencompleta;
    private String tipregistroorden;
    private LocalDate fecplanposteoinicio;
    private LocalDate fecplanposteofin;
    private LocalDate fecreacionregistroinicio;
    private LocalDate fecreacionregistrofin;


}
