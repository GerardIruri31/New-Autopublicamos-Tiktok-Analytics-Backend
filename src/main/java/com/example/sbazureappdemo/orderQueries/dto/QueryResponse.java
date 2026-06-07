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
    private String nCodlibro;
    private String tippublicacion;
    private String nTippublicacion;
    private String codescena;
    private String nCodescena;
    private String codposteador;
    private String codtelefono;
    private String codcuentatiktok;
    private String codsonido;
    private Integer nCodsonido;

    private String desscenahook;
    private String descaption;
    private String destropo;
    private String desslide1keywordshide;
    private String desslide2keywordshide;
    private String deshashtag;
    private String despalote;

    private String codimagenprincipal;
    private Integer nCodimagenprincipal;
    private String codimagenscreenshot;
    private Integer nCodimagenscreenshot;
    private String codimagendialogo;
    private Integer nCodimagendialogo;
    private String codvideo;
    private Integer nCodvideo;


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
