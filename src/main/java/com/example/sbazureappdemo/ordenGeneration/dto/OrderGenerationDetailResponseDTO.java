package com.example.sbazureappdemo.ordenGeneration.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;
import java.sql.Time;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderGenerationDetailResponseDTO {

    private Long codordentrabajo;
    private Long codcabeceraordentrabajo;

    private String codautora;

    @JsonProperty("nCodautora")
    private String nCodautora;

    private String codlibro;

    @JsonProperty("nCodlibro")
    private String nCodlibro;

    private String tippublicacion;


    private String codescenaauto;
    private String codescena;

    @JsonProperty("nCodescena")
    private String nCodescena;

    private String codposteador;

    @JsonProperty("nCodposteador")
    private String nCodposteador;

    private String codtelefono;
    private String codcuentatiktok;

    private Integer codsonido;

    @JsonProperty("nCodsonido")
    private String nCodsonido;

    private String desscenahook;
    private String descaption;
    private String destropo;
    private String desslide1keywordshide;
    private String desslide2keywordshide;
    private String deshashtag;
    private String despalote;

    private Long codimagenprincipal;

    @JsonProperty("nCodimagenprincipal")
    private String nCodimagenprincipal;

    private Long codimagenscreenshot;

    @JsonProperty("nCodimagenscreenshot")
    private String nCodimagenscreenshot;

    private Long codimagendialogo;

    @JsonProperty("nCodimagendialogo")
    private String nCodimagendialogo;

    private Long codvideo;

    @JsonProperty("nCodvideo")
    private String nCodvideo;

    private String desinstrucciones;
    private Date fecplanposteo;

    private String codestadoorden;
    private String tipregistroorden;
    private String flgordencompleta;

    private Integer ctddatoobligincompleto;
    private String desdatoobligincompleto;
    private String deslogerrororden;

    private String codusuarioauditoriacreareg;
    private String codusuarioauditoriaactualizareg;

    private Date fecreacionregistro;
    private Time horacreacionregistro;
    private Date fecactualizacionregistro;
    private Time horaactualizacionregistro;
}