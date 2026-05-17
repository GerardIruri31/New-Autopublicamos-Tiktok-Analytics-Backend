package com.example.sbazureappdemo.apifycall.dto;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import org.springframework.context.annotation.Scope;
import java.text.ParseException;
import java.sql.Date;
import java.sql.Time;

// Esta clase es un componente de Spring con un alcance de prototipo, lo que significa que cada vez que se inyecta, se crea una nueva instancia.
@Data
public class TiktokMetricasDTO {
    private String codpublicacion;
    private String codautora;
    private String codescena;
    private String codlibro;
    private String numescena;
    private String tippublicacion;
    private String codposteador;
    private String fecpublicacion;
    private String horapublicacion;
    private String nbrcuentatiktok;
    private String urlpublicacion;
    private int numviews;
    private int numlikes;
    private int numsaves;
    private int numreposts;
    private int numcomments;
    private double numengagement;
    private int numinteractions;
    private String deshashtags;
    private int nrohashtag;
    private String urlsounds;
    private String codregionposteo;
    private String fecreacionregistro;
    private String horacreacionregistro;
    private String userIdentification;


    public Date getFecpublicacionAsDate() {
        return parseSqlDate(fecpublicacion);
    }

    public Time getHorapublicacionAsTime() {
        return parseSqlTime(horapublicacion);
    }

    public Date getFecreacionregistroAsDate() {
        return parseSqlDate(fecreacionregistro);
    }

    public Time getHoracreacionregistroAsTime() {
        return parseSqlTime(horacreacionregistro);
    }

    private Date parseSqlDate(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = formatter.parse(value);
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    private Time parseSqlTime(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            java.util.Date utilDate = formatter.parse(value);
            return new Time(utilDate.getTime());
        } catch (ParseException e) {
            return null;
        }
    }
}
