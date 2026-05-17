package com.example.sbazureappdemo.paGraphs.repository;


import com.example.sbazureappdemo.paGraphs.dto.PaParamsFiltersDTO;
import com.example.sbazureappdemo.paGraphs.service.PaGraphsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PaGraphsRepository {

    Logger logger = LoggerFactory.getLogger(PaGraphsService.class);
    private final JdbcTemplate jdbc;
    public List<Map<String,Object>> GetDatosPaGraphConnectionDB(PaParamsFiltersDTO filtros) {
        String placeholders = filtros.getPAList().stream().map(p->"?").collect(Collectors.joining(", "));
        String sql =String.format("""
        SELECT
            T.codposteador, T.nbrPosteador, T.fecinicioperiodometa, T.fecfinperiodometa, T.numpostemeta,
            R.ctdPublicaciones as numpostereal, 

            CASE
                WHEN T.numpostemeta = 0 THEN 0
                ELSE ROUND((CAST(R.ctdPublicaciones AS NUMERIC)/CAST(T.numpostemeta AS NUMERIC))*100,0)
            END AS Eficacia,
            R.promNumviews, R.promNumlikes, R.promNumsaves, 
            R.promNumreposts, R.promNumcomments, R.promNumengagement, R.promInteraction
        FROM
        (
            SELECT po.codposteador, 
            COALESCE(po.nbposteador, '') || ' ' || COALESCE(po.apepatposteador, '') as nbrPosteador,
            pm.fecinicioperiodometa, pm.fecfinperiodometa, pm.numpostemeta
            FROM 
            m_posteadorasistente po 
            left join m_metaposteadorasistente pm 
            on po.codposteador = pm.codposteador
            and pm.fecinicioperiodometa = ?
            and pm.fecfinperiodometa = ?
            WHERE
            po.codposteador in (%s)
        ) T
        LEFT JOIN
        (
            select A.codposteador, count(a.codpublicacion) ctdPublicaciones, 
                    ROUND(avg(a.numviews),0) as promNumviews, ROUND(avg(a.numlikes),2) as promNumlikes, ROUND(avg(a.numsaves),2) as promNumsaves, 
                    ROUND(avg(a.numreposts),2) as promNumreposts, ROUND(avg(a.numcomments),2) as promNumcomments,
                    ROUND(avg(a.numengagement),2) as promNumengagement,
                    ROUND(avg(a.numinteractions),0) as promInteraction
            FROM
                (
                select 
                mp.codpublicacion, mp.codposteador, mp.fecpublicacion, mp.horapublicacion,
                mp.numviews, mp.numlikes, mp.numsaves, mp.numreposts, mp.numcomments, mp.numengagement, mp.numinteractions
                from
                h_metricapublicacion mp inner join
                ( 	SELECT
                        mpint.codpublicacion, max(mpint.fecreacionregistro+mpint.horacreacionregistro) as max_fecreacionregistro
                    from
                        h_metricapublicacion mpint
                    WHERE
                        mpint.fecpublicacion BETWEEN ? and ? AND
                        mpint.codposteador in (%s)
                    GROUP BY
                        mpint.codpublicacion ) base
                on mp.codpublicacion = base.codpublicacion and (mp.fecreacionregistro+mp.horacreacionregistro) = base.max_fecreacionregistro		
                where mp.numviews >= 100
                ) A
            GROUP BY 
            A.codposteador
        ) R
        ON T.codposteador = R.codposteador
        """, placeholders, placeholders);
        List<Object> params= new ArrayList<>() ;
        params.add(java.sql.Date.valueOf(filtros.getStartDate()));
        params.add(java.sql.Date.valueOf(filtros.getFinishDate()));
        params.addAll(filtros.getPAList());

        params.add(java.sql.Date.valueOf(filtros.getStartDate()));
        params.add(java.sql.Date.valueOf(filtros.getFinishDate()));
        params.addAll(filtros.getPAList());

        logger.info("Datos de los PA's obtenidos completadas con éxito");
        return jdbc.queryForList(sql,params.toArray());
    }
}
