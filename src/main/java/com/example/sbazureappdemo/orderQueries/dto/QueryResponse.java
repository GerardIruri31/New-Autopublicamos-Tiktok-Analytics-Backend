package com.example.sbazureappdemo.orderQueries.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
public class QueryResponse {

    private Integer codordentrabajo;
    private Integer codcabeceraordentrabajo;


    private String codautora;
    private String codlibro;
    private String tippublicacion;
    private String nTippublicacion;
    private String codescena;

    private String codposteador;
    private String codtelefono;
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
    private String flgordencompleta;

    private Integer ctddatoobligincompleto;
    private String desdatoobligincompleto;
    private String deslogerrororden;


    private String codusuarioauditoriacreareg;
    private String codusuarioauditoriaactualizareg;

    private LocalDate fecreacionregistro;
    private LocalTime horacreacionregistro;
    private LocalDate fecactualizacionregistro;
    private LocalTime horaactualizacionregistro;

}
