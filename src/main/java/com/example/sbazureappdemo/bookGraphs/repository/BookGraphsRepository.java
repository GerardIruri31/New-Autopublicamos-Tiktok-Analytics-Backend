package com.example.sbazureappdemo.bookGraphs.repository;

import com.example.sbazureappdemo.bookGraphs.dto.BookGraphsRequestDTO;
import com.example.sbazureappdemo.bookGraphs.dto.EfectividadBookMetaDTO;
import com.example.sbazureappdemo.bookGraphs.dto.RegistroMesLibroDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class BookGraphsRepository {
    Logger logger = LoggerFactory.getLogger(BookGraphsRepository.class);
    private final JdbcTemplate jdbc;

    public List<RegistroMesLibroDTO> dataBooksPerMonth(BookGraphsRequestDTO filtros) {
        String inClause = filtros.getBookList().stream().map(a -> "?").collect(Collectors.joining(",", "(", ")"));
        String sql = String.format("""
        SELECT R.mes, R.codlibro,
               CASE 
                   WHEN lb.deslibro IS NULL THEN 'sin nombre'
                   ELSE COALESCE(lb.deslibro, '')
               END AS deslibro,
               R.promNumviews, R.promInteraction, R.promNumengagement
        FROM (
            SELECT to_char(A.fecpublicacion, 'Mon-YY') AS mes,
                   A.codlibro,
                   COUNT(A.codpublicacion)                AS ctdPublicaciones,
                   ROUND(AVG(A.numviews),        2)       AS promNumviews,
                   ROUND(AVG(A.numengagement),   2)       AS promNumengagement,
                   ROUND(AVG(A.numinteractions), 2)       AS promInteraction
            FROM (
                SELECT mp.codpublicacion, mp.codlibro, mp.fecpublicacion, mp.horapublicacion,
                       mp.numviews, mp.numengagement, mp.numinteractions
                FROM h_metricapublicacion mp
                INNER JOIN (
                    SELECT mpint.codpublicacion,
                           MAX(mpint.fecreacionregistro + mpint.horacreacionregistro) AS max_fecreacionregistro
                    FROM h_metricapublicacion mpint
                    WHERE mpint.fecpublicacion BETWEEN ? AND ?
                      AND mpint.codlibro IN %s
                    GROUP BY mpint.codpublicacion
                ) base
                  ON mp.codpublicacion = base.codpublicacion
                 AND (mp.fecreacionregistro + mp.horacreacionregistro) = base.max_fecreacionregistro
                WHERE mp.numviews >= 100
            ) A
            GROUP BY to_char(A.fecpublicacion, 'Mon-YY'), A.codlibro
        ) R
        LEFT JOIN m_libro lb
          ON R.codlibro = lb.codlibro
        """, inClause);

        // Orden de parámetros: fechas + lista IN
        List<Object> params = new ArrayList<>();
        params.add(java.sql.Date.valueOf(filtros.getStartDate())); // BETWEEN ? (inicio)
        params.add(java.sql.Date.valueOf(filtros.getFinishDate()));    // BETWEEN ? (fin)
        params.addAll(filtros.getBookList());                           // IN (?, ?, ...)
        logger.info("Registros de Books obtenidos correctamente");

        return jdbc.query(sql, params.toArray(), (rs, i) -> {
            RegistroMesLibroDTO r = new RegistroMesLibroDTO();
            r.setMes(rs.getString("mes"));
            r.setCodlibro(rs.getString("codlibro"));
            r.setDeslibro(rs.getString("deslibro"));
            r.setPromNumviews(rs.getDouble("promNumviews"));
            r.setPromInteraction(rs.getDouble("promInteraction"));
            r.setPromNumengagement(rs.getDouble("promNumengagement"));
            return r;
        });
    }


    public List<EfectividadBookMetaDTO> effectivenessBooksPerMonth(BookGraphsRequestDTO filtros) {
        String inClause = filtros.getBookList().stream().map(x -> "?").collect(Collectors.joining(",", "(", ")"));
        String sql = String.format("""
        WITH meses AS (
            SELECT
                mes::date AS mes_dt,
                to_char(mes, 'Mon-YY') AS codmes
            FROM generate_series(
                date_trunc('month', ?::date),
                date_trunc('month', ?::date),
                interval '1 month'
            ) AS mes
        ),
        libros AS (
            SELECT
                lb.codlibro,
                lb.deslibro
            FROM m_libro lb
            WHERE lb.codlibro IN %s
        ),
        base AS (
            SELECT
                mpint.codpublicacion,
                mpint.codlibro,
                MAX(mpint.fecreacionregistro + mpint.horacreacionregistro) AS max_creacion_ts
            FROM h_metricapublicacion mpint
            WHERE mpint.fecpublicacion BETWEEN ? AND ?
              AND mpint.codlibro IN %s
            GROUP BY mpint.codpublicacion, mpint.codlibro
        ),
        A AS (
            SELECT
                mp.codpublicacion,
                mp.codlibro,
                mp.fecpublicacion
            FROM h_metricapublicacion mp
            INNER JOIN base
                ON mp.codpublicacion = base.codpublicacion
               AND mp.codlibro       = base.codlibro
               AND (mp.fecreacionregistro + mp.horacreacionregistro) = base.max_creacion_ts
            WHERE mp.numviews >= 100
        ),
        conteo AS (
            SELECT
                to_char(A.fecpublicacion, 'Mon-YY') AS codmes,
                A.codlibro,
                COUNT(A.codpublicacion) AS numposts
            FROM A
            GROUP BY to_char(A.fecpublicacion, 'Mon-YY'), A.codlibro
        )
        SELECT
            l.codlibro,
            l.deslibro,
            m.codmes,
            COALESCE(c.numposts, 0) AS numposts
        FROM libros l
        CROSS JOIN meses m
        LEFT JOIN conteo c
            ON c.codlibro = l.codlibro
           AND c.codmes   = m.codmes
        ORDER BY
            m.mes_dt,
            l.deslibro
        """, inClause, inClause);


        List<Object> params = new ArrayList<>();
        params.add(java.sql.Date.valueOf(filtros.getStartDate())); // generate_series start
        params.add(java.sql.Date.valueOf(filtros.getFinishDate()));    // generate_series end
        params.addAll(filtros.getBookList());                           // IN (metapostlibro)
        params.add(java.sql.Date.valueOf(filtros.getStartDate())); // BETWEEN start
        params.add(java.sql.Date.valueOf(filtros.getFinishDate()));    // BETWEEN end
        params.addAll(filtros.getBookList());                           // IN (subquery métricas)
        logger.info("Registros de Books obtenidos correctamente");

        return jdbc.query(sql, params.toArray(), (rs, i) -> {
            EfectividadBookMetaDTO dto = new EfectividadBookMetaDTO();
            dto.setCodlibro(rs.getString("codlibro"));
            dto.setDeslibro(rs.getString("deslibro"));
            dto.setCodmes(rs.getString("codmes"));
            dto.setNumposts(rs.getInt("numposts"));
            return dto;
        });
    }


}
