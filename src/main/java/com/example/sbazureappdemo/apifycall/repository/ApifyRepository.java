package com.example.sbazureappdemo.apifycall.repository;


import com.example.sbazureappdemo.apifycall.dto.TiktokMetricasDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ApifyRepository {
    Logger logger = LoggerFactory.getLogger(ApifyRepository.class);
    private final JdbcTemplate jdbc;

    public void saveAll(List<TiktokMetricasDTO> metricas) {
        try {
            //  Sentencia SQL para la inserción de datos en la tabla h_metricapublicacion
            String sql = "INSERT INTO h_metricapublicacion (" +
                    "codpublicacion, codautora, codescena, codlibro, numescena, tippublicacion, " +
                    "codposteador, fecpublicacion, horapublicacion, nbrcuentatiktok, urlpublicacion, " +
                    "numviews, numlikes, numsaves, numreposts, numcomments, " +
                    "numengagement, numinteractions, deshashtags, nrohashtag, urlsounds, codregionposteo, fecreacionregistro, horacreacionregistro, codusuarioauditoria" +
                    ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            // Lista para almacenar los parámetros de cada fila a insertar
            List<Object[]> batchArgs = new ArrayList<>();

            // Recorrer la lista de métricas y agregar los valores de cada objeto en batchArgs
            for (TiktokMetricasDTO m : metricas) {
                batchArgs.add(new Object[]{
                        m.getCodpublicacion(),
                        m.getCodautora(),
                        m.getCodescena(),
                        m.getCodlibro(),
                        m.getNumescena(),
                        m.getTippublicacion(),
                        m.getCodposteador(),
                        m.getFecpublicacionAsDate(),    // Convierte String a java.sql.Date
                        m.getHorapublicacionAsTime(),   // Convierte String a java.sql.Time
                        m.getNbrcuentatiktok(),
                        m.getUrlpublicacion(),
                        m.getNumviews(),
                        m.getNumlikes(),
                        m.getNumsaves(),
                        m.getNumreposts(),
                        m.getNumcomments(),
                        m.getNumengagement(),
                        m.getNuminteractions(),
                        m.getDeshashtags(),
                        m.getNrohashtag(),
                        m.getUrlsounds(),
                        m.getCodregionposteo(),
                        m.getFecreacionregistroAsDate(),        // Convierte String a java.sql.Date
                        m.getHoracreacionregistroAsTime(),       // Convierte String a java.sql.Time
                        m.getUserIdentification()
                });
            }

            // Se usa `batchUpdate` para insertar múltiples registros de manera eficiente.
            //  arreglo de enteros donde cada posición indica el número de filas afectadas por cada ejecución del batch
            int[] insertedRows = jdbc.batchUpdate(sql, batchArgs);
            logger.info("Registros insertados en BD - ApifyCall batch: " + Arrays.stream(insertedRows).sum());
        } catch (Exception e) {
            logger.error("Error al insertar registros en la BD - ApifyCall batch", e);
        }
    }

    public List<Map<String,Object>> getLastProcessedDataFromApifyConnection(String startDate, String finishDate, String trackStartDate, List<String> accountList) {
        String placeholders = accountList.stream().map(p->"?").collect(Collectors.joining(", "));
        String sql = String.format("""
            select 
                mp.codpublicacion AS "Post Code",
                mp.codautora AS "Author Code",
                COALESCE(a.nbautora, 'Not found: N/A') || ' ' || COALESCE(a.apeautora, '') AS "Author name",
                mp.codlibro AS "Book Code",
                COALESCE(lb.deslibro, 'Not found: N/A') AS "Book name",
                mp.codescena AS "Scene Code",
                COALESCE(esc.desscena, 'Not found: N/A') AS "Scene name",
                mp.numescena AS "Number of Scene",
                mp.tippublicacion AS "Code Post Type",
                COALESCE(tp.despost, 'Not found: N/A') AS "Post Type",
                mp.codposteador AS "PA Code",
                COALESCE(po.nbposteador, 'Not found: N/A') || ' ' || COALESCE(po.apepatposteador, '') || ' ' || COALESCE(po.apematposteador, '') AS "PA name",
                mp.fecpublicacion AS "Date posted",
                mp.horapublicacion AS "Time posted",
                mp.nbrcuentatiktok AS "TikTok Username",
                mp.urlpublicacion AS "Post URL",
                mp.numviews AS "Views",
                mp.numlikes AS "Likes",
                mp.numcomments AS "Comments",
                mp.numreposts AS "Reposted",
                mp.numsaves AS "Saves",
                mp.numengagement AS "Engagement rate",
                mp.numinteractions AS "Interactions",
                mp.deshashtags AS "Hashtags",
                mp.nrohashtag AS "Number of Hashtags",
                mp.urlsounds AS "Sound URL",
                mp.codregionposteo AS "Region Code",
                mp.fecreacionregistro AS "Tracking date",
                mp.horacreacionregistro AS "Tracking time",
                mp.codusuarioauditoria AS "Logged-in User"
        from
        h_metricapublicacion mp inner join ( 
            SELECT mpint.codpublicacion, max(mpint.fecreacionregistro+mpint.horacreacionregistro) as max_fecreacionregistro
            from h_metricapublicacion mpint
            WHERE
                mpint.fecpublicacion BETWEEN ? AND ? AND 
                mpint.fecreacionregistro BETWEEN ? AND ? AND
                mpint.nbrcuentatiktok in (%s)
            GROUP BY
                mpint.codpublicacion ) base
        on mp.codpublicacion = base.codpublicacion and (mp.fecreacionregistro+mp.horacreacionregistro) = base.max_fecreacionregistro
        left join m_autora a on mp.codautora = a.codautora
        left join m_escenalibro esc on mp.codescena = esc.codescena
        left join m_libro lb on mp.codlibro = lb.codlibro
        left join m_tipopost tp on mp.tippublicacion = tp.tippublicacion
        left join m_posteadorasistente po on mp.codposteador = po.codposteador
                """,placeholders);


        List<Object> params= new ArrayList<>();
        params.add(java.sql.Date.valueOf(startDate));
        params.add(java.sql.Date.valueOf(finishDate));
        java.time.LocalDate trackStart = java.time.LocalDate.parse(trackStartDate);
        params.add(java.sql.Date.valueOf(trackStart.minusDays(1)));
        params.add(java.sql.Date.valueOf(trackStart.plusDays(1)));
        params.addAll(accountList);
        return jdbc.queryForList(sql,params.toArray());
    }


}
