package com.example.sbazureappdemo.authorGraphs.repository;

import com.example.sbazureappdemo.authorGraphs.dto.AuthorGraphsDTO;
import com.example.sbazureappdemo.authorGraphs.dto.EfectividadAutorMetaDTO;
import com.example.sbazureappdemo.authorGraphs.dto.RegistroMesAutoraDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AuthorGraphsRepository {
    Logger logger = LoggerFactory.getLogger(AuthorGraphsRepository.class);
    private final JdbcTemplate jdbc;

    public List<RegistroMesAutoraDTO> dataAuthorsPerMonth(AuthorGraphsDTO filtros) {
        String inClause = filtros.getAuthorList().stream().map(a -> "?").collect(Collectors.joining(",", "(", ")"));
        String sql = String.format("""
        SELECT R.mes, R.codautora,
               CASE 
                   WHEN au.nbautora IS NULL AND au.apeautora IS NULL THEN 'sin nombre'
                   ELSE COALESCE(au.nbautora, '') || CHR(10) || COALESCE(au.apeautora, '')
               END AS nbrAutora,
               R.promNumviews, R.promInteraction, R.promNumengagement
        FROM (
            SELECT to_char(A.fecpublicacion, 'Mon-YY') AS mes,
                   A.codautora,
                   COUNT(A.codpublicacion) AS ctdPublicaciones,
                   ROUND(AVG(A.numviews), 2)        AS promNumviews,
                   ROUND(AVG(A.numengagement), 2)   AS promNumengagement,
                   ROUND(AVG(A.numinteractions), 2) AS promInteraction
            FROM (
                SELECT mp.codpublicacion, mp.codautora, mp.fecpublicacion, mp.horapublicacion,
                       mp.numviews, mp.numengagement, mp.numinteractions
                FROM h_metricapublicacion mp
                INNER JOIN (
                    SELECT mpint.codpublicacion,
                           MAX(mpint.fecreacionregistro + mpint.horacreacionregistro) AS max_fecreacionregistro
                    FROM h_metricapublicacion mpint
                    WHERE mpint.fecpublicacion BETWEEN ? AND ?
                      AND mpint.codautora IN %s
                    GROUP BY mpint.codpublicacion
                ) base
                  ON mp.codpublicacion = base.codpublicacion
                 AND (mp.fecreacionregistro + mp.horacreacionregistro) = base.max_fecreacionregistro
                WHERE mp.numviews >= 100
            ) A
            GROUP BY to_char(A.fecpublicacion, 'Mon-YY'), A.codautora
        ) R
        LEFT JOIN m_autora au
          ON R.codautora = au.codautora
        """, inClause);

        List<Object> params = new ArrayList<>();
        params.add(java.sql.Date.valueOf(filtros.getStartDate())); // BETWEEN ? (inicio)
        params.add(java.sql.Date.valueOf(filtros.getFinishDate()));    // BETWEEN ? (fin)
        params.addAll(filtros.getAuthorList());                          // IN (?, ?, ...)
        logger.info("Registros de Authors obtenidos correctamente");


        return jdbc.query(sql, params.toArray(), (rs, i) -> {
            RegistroMesAutoraDTO r = new RegistroMesAutoraDTO();
            r.setMes(rs.getString("mes"));
            r.setCodautora(rs.getString("codautora"));
            r.setNbrAutora(rs.getString("nbrAutora"));
            r.setPromNumviews(rs.getDouble("promNumviews"));
            r.setPromInteraction(rs.getDouble("promInteraction"));
            r.setPromNumengagement(rs.getDouble("promNumengagement"));
            return r;
        });
    }


    public List<Map<String,Object>> GetDatosAuthorGraphsConnectionDBQuery1(AuthorGraphsDTO filtros) {
        String placeholders_authors = filtros.getAuthorList().stream().map(p->"?").collect(Collectors.joining(", "));
        String sql =String.format("""
        SELECT
            T.codautora, T.nbrAutora,
            R.promNumviews, R.promNumlikes, R.promNumsaves, 
            R.promNumreposts, R.promNumcomments, R.promNumengagement, R.promInteraction
            FROM
        (   
            SELECT au.codautora,
            COALESCE(au.nbautora, '') || ' ' || COALESCE(au.apeautora, '') as nbrAutora
            FROM m_autora au 
            WHERE au.codautora in (%s)
        ) T
        LEFT JOIN  (
            select A.codautora, count(A.codpublicacion) ctdPublicaciones, 
                    ROUND(avg(A.numviews),0) as promNumviews, ROUND(avg(A.numlikes),2) as promNumlikes, ROUND(avg(A.numsaves),2) as promNumsaves, 
                    ROUND(avg(A.numreposts),2) as promNumreposts, ROUND(avg(A.numcomments),2) as promNumcomments,
                    ROUND(avg(A.numengagement),0) as promNumengagement, ROUND(avg(a.numinteractions),0) as promInteraction
            FROM  (
                    select 
                        mp.codpublicacion, mp.codautora, mp.fecpublicacion, mp.horapublicacion,
                        mp.numviews, mp.numlikes, mp.numsaves, mp.numreposts, mp.numcomments, mp.numengagement, mp.numinteractions
                    from h_metricapublicacion mp 
                    inner join  ( 	
                        SELECT
                            mpint.codpublicacion, max(mpint.fecreacionregistro+mpint.horacreacionregistro) as max_fecreacionregistro
                        from
                            h_metricapublicacion mpint
                        WHERE
                            mpint.fecpublicacion BETWEEN ? and ? AND
                            mpint.codautora in (%s)
                        GROUP BY
                            mpint.codpublicacion ) base
                    on mp.codpublicacion = base.codpublicacion and (mp.fecreacionregistro+mp.horacreacionregistro) = base.max_fecreacionregistro
                    where mp.numviews >= 100
                    ) A
            GROUP BY 
            A.codautora
        ) R
        ON T.codautora = R.codautora
    """, placeholders_authors, placeholders_authors);
        List<Object> params= new ArrayList<>() ;
        params.addAll(filtros.getAuthorList());
        params.add(java.sql.Date.valueOf(filtros.getStartDate()));
        params.add(java.sql.Date.valueOf(filtros.getFinishDate()));
        params.addAll(filtros.getAuthorList());
        logger.info("Registros de Authors obtenidos correctamente");
        return jdbc.queryForList(sql,params.toArray());

    }


    public List<Map<String,Object>> GetDatosAuthorGraphsConnectionDBQuery2(AuthorGraphsDTO filtros) {
        String placeholders_authors = filtros.getAuthorList().stream().map(p->"?").collect(Collectors.joining(", "));
        String sql =String.format("""
        SELECT
            T.codautora, T.nbrAutora,
            R.SumNumviews,R.fecpublicacion
        FROM
        (   
            SELECT au.codautora,
            COALESCE(au.nbautora, '') || ' ' || COALESCE(au.apeautora, '') as nbrAutora
            FROM m_autora au 
            WHERE au.codautora in (%s)
        ) T
        LEFT JOIN  (
            select 
                A.codautora, count(A.codpublicacion) ctdPublicaciones,
                SUM(A.numviews) as SumNumviews, A.fecpublicacion
            FROM
                (
                select mp.codpublicacion, mp.codautora, mp.fecpublicacion, mp.horapublicacion,
                mp.numviews
                    from h_metricapublicacion mp inner join  ( 	
                        SELECT
                            mpint.codpublicacion, max(mpint.fecreacionregistro+mpint.horacreacionregistro) as max_fecreacionregistro
                        from
                            h_metricapublicacion mpint
                        WHERE
                            mpint.fecpublicacion BETWEEN ? and ? AND
                            mpint.codautora in (%s)
                        GROUP BY
                            mpint.codpublicacion ) base
                    on mp.codpublicacion = base.codpublicacion and (mp.fecreacionregistro+mp.horacreacionregistro) = base.max_fecreacionregistro
                    where mp.numviews >= 100
                ) A
            GROUP BY 
            A.fecpublicacion, A.codautora
        ) R
        ON T.codautora = R.codautora
    """, placeholders_authors, placeholders_authors);
        List<Object> params= new ArrayList<>() ;
        params.addAll(filtros.getAuthorList());
        params.add(java.sql.Date.valueOf(filtros.getStartDate()));
        params.add(java.sql.Date.valueOf(filtros.getFinishDate()));
        params.addAll(filtros.getAuthorList());
        logger.info("Registros de Authors obtenidos correctamente");
        return jdbc.queryForList(sql,params.toArray());

    }


    public List<EfectividadAutorMetaDTO> effectDataAuthorsPerMonth(AuthorGraphsDTO filtros) {
        String inClause = filtros.getAuthorList().stream().map(a -> "?").collect(Collectors.joining(",", "(", ")"));
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
        autoras AS (
            SELECT
                a.codautora,
                CASE
                    WHEN a.nbautora IS NULL AND a.apeautora IS NULL THEN 'sin nombre'
                    ELSE COALESCE(a.nbautora, '') || CHR(10) || COALESCE(a.apeautora, '')
                END AS nbautora
            FROM m_autora a
            WHERE a.codautora IN %s
        ),
        base AS (
            SELECT
                mpint.codpublicacion,
                mpint.codautora,
                MAX(mpint.fecreacionregistro + mpint.horacreacionregistro) AS max_creacion_ts
            FROM h_metricapublicacion mpint
            WHERE mpint.fecpublicacion BETWEEN ? AND ?
              AND mpint.codautora IN %s
            GROUP BY mpint.codpublicacion, mpint.codautora
        ),
        A AS (
            SELECT
                mp.codpublicacion,
                mp.codautora,
                mp.fecpublicacion
            FROM h_metricapublicacion mp
            INNER JOIN base
                ON mp.codpublicacion = base.codpublicacion
               AND mp.codautora      = base.codautora
               AND (mp.fecreacionregistro + mp.horacreacionregistro) = base.max_creacion_ts
            WHERE mp.numviews >= 100
        ),
        conteo AS (
            SELECT
                to_char(A.fecpublicacion, 'Mon-YY') AS codmes,
                A.codautora,
                COUNT(A.codpublicacion) AS numposts
            FROM A
            GROUP BY to_char(A.fecpublicacion, 'Mon-YY'), A.codautora
        )
        SELECT
            au.codautora,
            au.nbautora,
            m.codmes,
            COALESCE(c.numposts, 0) AS numposts
        FROM autoras au
        CROSS JOIN meses m
        LEFT JOIN conteo c
            ON c.codautora = au.codautora
           AND c.codmes    = m.codmes
        ORDER BY
            m.mes_dt,
            au.nbautora
        """, inClause, inClause);

        // Parámetros en orden de aparición en los "?"
        List<Object> params = new ArrayList<>();
        params.add(Date.valueOf(filtros.getStartDate()));  // para ?::date inicio
        params.add(Date.valueOf(filtros.getFinishDate()));     // para ?::date fin
        params.addAll(filtros.getAuthorList());                 // IN (?, ?, ?)
        params.add(Date.valueOf(filtros.getStartDate()));  // BETWEEN ? (inicio)
        params.add(Date.valueOf(filtros.getFinishDate()));     // BETWEEN ? (fin)
        params.addAll(filtros.getAuthorList());                 // IN (?, ?, ?)
        logger.info("Registros de Authors obtenidos correctamente");

        return jdbc.query(sql, params.toArray(), (rs, rowNum) -> {
            EfectividadAutorMetaDTO dto = new EfectividadAutorMetaDTO();
            dto.setCodautora(rs.getString("codautora"));
            dto.setNbautora(rs.getString("nbautora"));
            dto.setCodmes(rs.getString("codmes"));
            dto.setNumposts(rs.getInt("numposts"));
            return dto;
        });
    }


}
