package com.example.sbazureappdemo.orderQueries.service;


import com.example.sbazureappdemo.dbQueries.repository.DbQueryRepository;
import com.example.sbazureappdemo.dbQueries.service.DbQueryService;
import com.example.sbazureappdemo.exceptions.ResourceNotFoundException;
import com.example.sbazureappdemo.ordenGeneration.dto.PaResponseDTO;
import com.example.sbazureappdemo.orderQueries.dto.CreatedByDTO;
import com.example.sbazureappdemo.orderQueries.dto.FiltersRequest;
import com.example.sbazureappdemo.orderQueries.dto.QueryResponse;
import com.example.sbazureappdemo.orderQueries.repository.OrderQueriesRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueriesService {
    Logger logger = LoggerFactory.getLogger(OrderQueriesService.class);
    private final OrderQueriesRepository orderQueriesRepository;
    private final OrderExcelQueries orderExcelQueries;


    public List<QueryResponse> search(FiltersRequest request) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        sql.append("""
            SELECT
                h.codordentrabajo                                           AS codordentrabajo,
                h.codcabeceraordentrabajo                                   AS codcabeceraordentrabajo,

                COALESCE(a.nbautora, '') ||
                CASE
                    WHEN a.apeautora IS NOT NULL AND a.apeautora <> '' THEN ' ' || a.apeautora
                    ELSE ''
                END                                                         AS codautora,

                h.codlibro                                                  AS codlibro,
                l.deslibro                                                  AS nCodlibro,
                e.tippublicacion AS tippublicacion,
                tp.despost AS nTippublicacion,

                h.codescena                                                 AS codescena,
                e.desscena                                                  AS nCodescena,
                
                COALESCE(p.nbposteador, '') ||
                CASE
                    WHEN p.apematposteador IS NOT NULL AND p.apematposteador <> '' THEN ' ' || p.apematposteador
                    ELSE ''
                END                                                         AS codposteador,

                h.codtelefono                                               AS codtelefono,
                h.codcuentatiktok                                           AS codcuentatiktok,
                
                s.urlsonido                                                 AS codsonido,
                s.codsonido                                                 AS nCodsonido,

                h.desscenahook                                              AS desscenahook,
                COALESCE(e.descaption, h.descaption)                        AS descaption,
                h.destropo                                                  AS destropo,
                h.desslide1keywordshide                                     AS desslide1keywordshide,
                h.desslide2keywordshide                                     AS desslide2keywordshide,
                h.deshashtag                                                AS deshashtag,
                h.despalote                                                 AS despalote,

                imgp.urlimagenvideo                                         AS codimagenprincipal,
                imgp.codimagenvideo                                         AS nCodimagenprincipal,
                imgs.urlimagenvideo                                         AS codimagenscreenshot,
                imgs.codimagenvideo                                         AS nCodimagenscreenshot,
                imgd.urlimagenvideo                                         AS codimagendialogo,
                imgd.codimagenvideo                                         AS nCodimagendialogo,
                imgv.urlimagenvideo                                         AS codvideo,
                imgv.codimagenvideo                                         AS nCodvideo,

                h.desinstrucciones                                          AS desinstrucciones,
                h.fecplanposteo                                             AS fecplanposteo,

                CASE h.codestadoorden
                    WHEN 1 THEN 'Assigned'
                    WHEN 2 THEN 'Flagged'
                    WHEN 3 THEN '-100 Views'
                    WHEN 4 THEN 'Posted'
                    WHEN 5 THEN 'Drafted'
                    WHEN 6 THEN 'Deleted'
                    ELSE NULL
                END                                                         AS codestadoorden,

                h.tipregistroorden                                          AS tipregistroorden,
                h.flgordencompleta                                          AS flgordencompleta,
                h.ctddatoobligincompleto                                    AS ctddatoobligincompleto,
                h.desdatoobligincompleto                                    AS desdatoobligincompleto,
                h.deslogerrororden                                          AS deslogerrororden,

                uc.codusuario                                               AS codusuarioauditoriacreareg,
                uu.codusuario                                               AS codusuarioauditoriaactualizareg,

                h.fecreacionregistro                                        AS fecreacionregistro,
                h.horacreacionregistro                                      AS horacreacionregistro,
                h.fecactualizacionregistro                                  AS fecactualizacionregistro,
                h.horaactualizacionregistro                                 AS horaactualizacionregistro

            FROM h_ordentrabajo h
            LEFT JOIN m_posteadorasistente p
                   ON p.codposteador = h.codposteador
            LEFT JOIN m_autora a
                   ON a.codautora = h.codautora
            LEFT JOIN m_libro l
                   ON l.codlibro = h.codlibro
            LEFT JOIN m_escenalibro e
                   ON e.codescena = h.codescena
            LEFT JOIN m_tipopost tp 
                   on tp.tippublicacion = e.tippublicacion
            LEFT JOIN m_sonido s
                   ON s.codsonido = h.codsonido
            LEFT JOIN m_imagenvideo imgp
                   ON imgp.codimagenvideo = h.codimagenprincipal
            LEFT JOIN m_imagenvideo imgs
                   ON imgs.codimagenvideo = h.codimagenscreenshot
            LEFT JOIN m_imagenvideo imgd
                   ON imgd.codimagenvideo = h.codimagendialogo
            LEFT JOIN m_imagenvideo imgv
                   ON imgv.codimagenvideo = h.codvideo
            LEFT JOIN m_usuariorol uc
                   ON uc.codusuario = h.codusuarioauditoriacreareg
            LEFT JOIN m_usuariorol uu
                   ON uu.codusuario = h.codusuarioauditoriaactualizareg
          
            WHERE 1 = 1
        """);

        addStringCondition(sql, params, "h.codposteador", request.getCodposteador());
        addStringCondition(sql, params, "h.codtelefono", request.getCodtelefono());
        addStringCondition(sql, params, "h.codautora", request.getCodautora());
        addStringCondition(sql, params, "h.codlibro", request.getCodlibro());
        addStringCondition(sql, params, "h.codescena", request.getCodescena());
        addStringCondition(sql, params, "e.tippublicacion", request.getTippublicacion());
        addIntegerCondition(sql, params, "h.codsonido", request.getCodsonido());
        addStringCondition(sql, params, "h.codcuentatiktok", request.getCodcuentatiktok());
        addStringCondition(sql, params, "h.codusuarioauditoriacreareg", request.getCodusuarioauditoriacreareg());
        addIntegerCondition(sql, params, "h.codestadoorden", request.getCodestadoorden());
        addStringCondition(sql, params, "h.flgordencompleta", request.getFlgordencompleta());
        addStringCondition(sql, params, "h.tipregistroorden", request.getTipregistroorden());

        addBetweenDates(sql, params, "h.fecplanposteo", request.getFecplanposteoinicio(), request.getFecplanposteofin());
        addBetweenDates(sql, params, "h.fecreacionregistro", request.getFecreacionregistroinicio(), request.getFecreacionregistrofin());

        sql.append(" ORDER BY h.codordentrabajo DESC ");
        System.out.println(sql);
        List<QueryResponse> dto = orderQueriesRepository.search(sql, params);
        System.out.println(dto);
        return dto;
    }

    public List<CreatedByDTO> createdBy() {
        return orderQueriesRepository.createdBy();
    }


    public byte[] downloadExcel(FiltersRequest request) {
        List<QueryResponse> data = search(request);
        if (data.isEmpty()) throw new ResourceNotFoundException("Lista de órdenes a importar está vacía");
        return orderExcelQueries.downloadExcel(data);
    }



    private void addStringCondition(StringBuilder sql, List<Object> params, String column, String value) {
        if (value != null && !value.trim().isEmpty()) {
            sql.append(" AND ").append(column).append(" = ? ");
            params.add(value.trim());
        }
    }

    private void addIntegerCondition(StringBuilder sql, List<Object> params, String column, Integer value) {
        if (value != null) {
            sql.append(" AND ").append(column).append(" = ? ");
            params.add(value);
        }
    }

    private void addBetweenDates(
            StringBuilder sql,
            List<Object> params,
            String column,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (startDate != null && endDate != null) {
            sql.append(" AND ").append(column).append(" BETWEEN ? AND ? ");
            params.add(startDate);
            params.add(endDate);
        } else if (startDate != null) {
            sql.append(" AND ").append(column).append(" >= ? ");
            params.add(startDate);
        } else if (endDate != null) {
            sql.append(" AND ").append(column).append(" <= ? ");
            params.add(endDate);
        }
    }




}
