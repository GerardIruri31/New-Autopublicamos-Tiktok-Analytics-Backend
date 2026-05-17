package com.example.sbazureappdemo.dbQueries.repository;


import com.example.sbazureappdemo.dbQueries.dto.FiltersRequestDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DbQueryRepository {
    Logger logger = LoggerFactory.getLogger(DbQueryRepository.class);
    private final JdbcTemplate jdbc;

    public List<Map<String,Object>> FilterConnection(FiltersRequestDTO request) {
        return FilterConnection(request, null);
    }

    public List<Map<String,Object>> FilterConnection(FiltersRequestDTO request, Integer limit) {
        boolean incluirCount = (limit != null && limit > 0);
        String sql = incluirCount
                ? """
            SELECT
              t.*,COUNT(*) OVER () AS "count"
            FROM (
          """
                : """
            SELECT t.*
            FROM (
          """;

        sql +=  """ 
        SELECT 
            mp.codpublicacion AS "Post Code",
            mp.codautora AS "Author Code",
            COALESCE(a.nbautora, 'Not found: N/A') || ' ' || COALESCE(a.apeautora, '') AS "Author name",
            mp.codlibro AS "Book Code",
            COALESCE(lb.deslibro, 'Not found: N/A') AS "Book name",
            mp.numescena AS "Number of Scene",
            mp.codescena AS "Scene Code",
            COALESCE(esc.desscena, 'Not found: N/A') AS "Scene name",
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
        FROM h_metricapublicacion mp
        INNER JOIN (
            SELECT 
                codpublicacion, 
                MAX(mpint.fecreacionregistro+mpint.horacreacionregistro) AS max_fecreacionregistro
            FROM h_metricapublicacion mpint
            WHERE 1=1
        """;

        List<Object> parametros = new ArrayList<>();
        if (!request.getPubStartDate().isEmpty() && !request.getPubFinishtDate().isEmpty()) {
            sql += " AND mpint.fecpublicacion BETWEEN ? AND ?";
            parametros.add(java.sql.Date.valueOf(request.getPubStartDate()));
            parametros.add(java.sql.Date.valueOf(request.getPubFinishtDate()));
        }

        if (!request.getTrackStartDate().isEmpty() && !request.getTrackFinishtDate().isEmpty()) {
            sql += " AND mpint.fecreacionregistro BETWEEN ? AND ?";
            parametros.add(java.sql.Date.valueOf(request.getTrackStartDate()));
            parametros.add(java.sql.Date.valueOf(request.getTrackFinishtDate()));
        }


        if (!request.getAuthorList().isEmpty()) {
            sql += " AND mpint.codautora IN (" + String.join(",", Collections.nCopies(request.getAuthorList().size(), "?")) + ")";
            parametros.addAll(request.getAuthorList());
        }

        if (!request.getBookList().isEmpty()) {
            sql += " AND mpint.codlibro IN (" + String.join(",", Collections.nCopies(request.getBookList().size(), "?")) + ")";
            parametros.addAll(request.getBookList());
        }

        if (!request.getPAList().isEmpty()) {
            sql += " AND mpint.codposteador IN (" + String.join(",", Collections.nCopies(request.getPAList().size(), "?")) + ")";
            parametros.addAll(request.getPAList());
        }

        if (!request.getSceneList().isEmpty()) {
            sql += " AND mpint.codescena IN (" + String.join(",", Collections.nCopies(request.getSceneList().size(), "?")) + ")";
            parametros.addAll(request.getSceneList());
        }

        if (!request.getTypePostList().isEmpty()) {
            sql += " AND mpint.tippublicacion IN (" + String.join(",", Collections.nCopies(request.getTypePostList().size(), "?")) + ")";
            parametros.addAll(request.getTypePostList());
        }

        if (!request.getAccountList().isEmpty()) {
            sql += " AND mpint.nbrcuentatiktok IN (" + String.join(",", Collections.nCopies(request.getAccountList().size(), "?")) + ")";
            parametros.addAll(request.getAccountList());
        }

        if (!request.getPostIDList().isEmpty()) {
            sql += " AND mpint.codpublicacion IN (" + String.join(",", Collections.nCopies(request.getPostIDList().size(), "?")) + ")";
            parametros.addAll(request.getPostIDList());
        }

        if (!request.getRegionList().isEmpty()) {
            sql += " AND mpint.codregionposteo IN (" + String.join(",", Collections.nCopies(request.getRegionList().size(), "?")) + ")";
            parametros.addAll(request.getRegionList());
        }

        sql += """
                GROUP BY mpint.codpublicacion
            ) base
            ON mp.codpublicacion = base.codpublicacion 
            AND (mp.fecreacionregistro+mp.horacreacionregistro) = base.max_fecreacionregistro
            LEFT JOIN m_autora a ON mp.codautora = a.codautora
            LEFT JOIN m_escenalibro esc ON mp.codescena = esc.codescena
            LEFT JOIN m_libro lb ON mp.codlibro = lb.codlibro
            LEFT JOIN m_tipopost tp ON mp.tippublicacion = tp.tippublicacion
            LEFT JOIN m_posteadorasistente po ON mp.codposteador = po.codposteador
            WHERE 1=1
        """;


        if (!request.getLikesMin().isEmpty() || !request.getLikesMax().isEmpty()) {
            sql += " AND mp.numlikes BETWEEN ? AND ?";
            parametros.add(request.getLikesMin().isEmpty() ? 0 : Integer.parseInt(request.getLikesMin()));
            parametros.add(request.getLikesMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getLikesMax()));
        }

        if (!request.getInteractionMin().isEmpty() || !request.getInteractionMax().isEmpty()) {
            sql += " AND mp.numinteractions BETWEEN ? AND ?";
            parametros.add(request.getInteractionMin().isEmpty() ? 0: Integer.parseInt(request.getInteractionMin()));
            parametros.add(request.getInteractionMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getInteractionMax()));
        }

        if (!request.getSavesMin().isEmpty() || !request.getSavesMax().isEmpty()) {
            sql += " AND mp.numsaves BETWEEN ? AND ?";
            parametros.add(request.getSavesMin().isEmpty() ? 0: Integer.parseInt(request.getSavesMin()));
            parametros.add(request.getSavesMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getSavesMax()));
        }

        if (!request.getCommentsMin().isEmpty() || !request.getCommentsMax().isEmpty()) {
            sql += " AND mp.numcomments BETWEEN ? AND ?";
            parametros.add(request.getCommentsMin().isEmpty() ? 0: Integer.parseInt(request.getCommentsMin()));
            parametros.add(request.getCommentsMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getCommentsMax()));
        }

        if (!request.getViewsMin().isEmpty() || !request.getViewsMax().isEmpty()) {
            sql += " AND mp.numviews BETWEEN ? AND ?";
            parametros.add(request.getViewsMin().isEmpty() ? 0: Integer.parseInt(request.getViewsMin()));
            parametros.add(request.getViewsMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getViewsMax()));
        }

        if (!request.getEngagementMin().isEmpty() || !request.getEngagementMax().isEmpty()) {
            sql += " AND mp.numengagement BETWEEN ? AND ?";
            parametros.add(request.getEngagementMin().isEmpty() ? 0: Double.parseDouble(request.getEngagementMin()));
            parametros.add(request.getEngagementMax().isEmpty() ? 100 : Double.parseDouble(request.getEngagementMax()));
        }

        sql += "\n) t";

        if (incluirCount) {
            sql += " LIMIT ?";
            parametros.add(limit);
        }
        logger.info("Registros obtenidos de BD correctamente - dbqueries");
        return jdbc.queryForList(sql.toString(),parametros.toArray());
    }

    public List<Map<String, Object>> scoreSceneConnectionV2(FiltersRequestDTO request) {
        return scoreSceneConnectionV2(request, null);
    }

    public List<Map<String, Object>> scoreSceneConnectionV2(FiltersRequestDTO request, Integer limit) {
        boolean incluirCount = (limit != null && limit > 0);
        StringBuilder sql = new StringBuilder("""
            WITH base AS (
                SELECT
                    mpint.codpublicacion,
                    MAX(mpint.fecreacionregistro + mpint.horacreacionregistro) AS max_fecreacionregistro
                FROM h_metricapublicacion mpint
                WHERE 1=1
            """);

        List<Object> parametros = new ArrayList<>();
        if (!request.getPubStartDate().isEmpty() && !request.getPubFinishtDate().isEmpty()) {
            sql.append(" AND mpint.fecpublicacion BETWEEN ? AND ?\n");
            parametros.add(java.sql.Date.valueOf(request.getPubStartDate()));
            parametros.add(java.sql.Date.valueOf(request.getPubFinishtDate()));
        }

        if (!request.getTrackStartDate().isEmpty() && !request.getTrackFinishtDate().isEmpty()) {
            sql.append(" AND mpint.fecreacionregistro BETWEEN ? AND ?\n");
            parametros.add(java.sql.Date.valueOf(request.getTrackStartDate()));
            parametros.add(java.sql.Date.valueOf(request.getTrackFinishtDate()));
        }

        if (!request.getAuthorList().isEmpty()) {
            sql.append(" AND mpint.codautora IN (").append(String.join(",", Collections.nCopies(request.getAuthorList().size(), "?"))).append(")\n");
            parametros.addAll(request.getAuthorList());
        }

        if (!request.getBookList().isEmpty()) {
            sql.append(" AND mpint.codlibro IN (").append(String.join(",", Collections.nCopies(request.getBookList().size(), "?"))).append(")\n");
            parametros.addAll(request.getBookList());
        }

        if (!request.getPAList().isEmpty()) {
            sql.append(" AND mpint.codposteador IN (").append(String.join(",", Collections.nCopies(request.getPAList().size(), "?"))).append(")\n");
            parametros.addAll(request.getPAList());
        }

        if (!request.getSceneList().isEmpty()) {
            sql.append(" AND mpint.codescena IN (").append(String.join(",", Collections.nCopies(request.getSceneList().size(), "?"))).append(")\n");
            parametros.addAll(request.getSceneList());
        }

        if (!request.getTypePostList().isEmpty()) {
            sql.append(" AND mpint.tippublicacion IN (").append(String.join(",", Collections.nCopies(request.getTypePostList().size(), "?"))).append(")\n");
            parametros.addAll(request.getTypePostList());
        }

        if (!request.getAccountList().isEmpty()) {
            sql.append(" AND mpint.nbrcuentatiktok IN (").append(String.join(",", Collections.nCopies(request.getAccountList().size(), "?"))).append(")\n");
            parametros.addAll(request.getAccountList());
        }

        if (!request.getPostIDList().isEmpty()) {
            sql.append(" AND mpint.codpublicacion IN (").append(String.join(",", Collections.nCopies(request.getPostIDList().size(), "?"))).append(")\n");
            parametros.addAll(request.getPostIDList());
        }

        if (!request.getRegionList().isEmpty()) {
            sql.append(" AND mpint.codregionposteo IN (").append(String.join(",", Collections.nCopies(request.getRegionList().size(), "?"))).append(")\n");
            parametros.addAll(request.getRegionList());
        }

        // Cierra el bloque del CTE “base”
        sql.append("""
                GROUP BY mpint.codpublicacion
            ),
            filtradas AS (
                SELECT
                    mp.codautora,
                    mp.codlibro,
                    mp.codescena,
                    mp.numviews,
                    mp.numinteractions,
                    mp.numlikes,
                    mp.numreposts,
                    mp.numsaves,
                    mp.numengagement
                FROM h_metricapublicacion mp
                INNER JOIN base
                  ON mp.codpublicacion = base.codpublicacion
                 AND (mp.fecreacionregistro + mp.horacreacionregistro) = base.max_fecreacionregistro
                WHERE 1=1
            """);

        if (!request.getLikesMin().isEmpty() || !request.getLikesMax().isEmpty()) {
            sql.append(" AND mp.numlikes BETWEEN ? AND ?\n");
            parametros.add(request.getLikesMin().isEmpty() ? 0 : Integer.parseInt(request.getLikesMin()));
            parametros.add(request.getLikesMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getLikesMax()));
        }

        if (!request.getInteractionMin().isEmpty() || !request.getInteractionMax().isEmpty()) {
            sql.append(" AND mp.numinteractions BETWEEN ? AND ?\n");
            parametros.add(request.getInteractionMin().isEmpty() ? 0 : Integer.parseInt(request.getInteractionMin()));
            parametros.add(request.getInteractionMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getInteractionMax()));
        }

        if (!request.getSavesMin().isEmpty() || !request.getSavesMax().isEmpty()) {
            sql.append(" AND mp.numsaves BETWEEN ? AND ?\n");
            parametros.add(request.getSavesMin().isEmpty() ? 0 : Integer.parseInt(request.getSavesMin()));
            parametros.add(request.getSavesMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getSavesMax()));
        }

        if (!request.getCommentsMin().isEmpty() || !request.getCommentsMax().isEmpty()) {
            sql.append(" AND mp.numcomments BETWEEN ? AND ?\n");
            parametros.add(request.getCommentsMin().isEmpty() ? 0: Integer.parseInt(request.getCommentsMin()));
            parametros.add(request.getCommentsMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getCommentsMax()));
        }

        if (!request.getViewsMin().isEmpty() || !request.getViewsMax().isEmpty()) {
            sql.append(" AND mp.numviews BETWEEN ? AND ?\n");
            parametros.add(request.getViewsMin().isEmpty() ? 0 : Integer.parseInt(request.getViewsMin()));
            parametros.add(request.getViewsMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getViewsMax()));
        }

        if (!request.getEngagementMin().isEmpty() || !request.getEngagementMax().isEmpty()) {
            sql.append(" AND mp.numengagement BETWEEN ? AND ?\n");
            parametros.add(request.getEngagementMin().isEmpty() ? 0.0 : Double.parseDouble(request.getEngagementMin()));
            parametros.add(request.getEngagementMax().isEmpty() ? 100.0 : Double.parseDouble(request.getEngagementMax()));
        }

        sql.append("""
            ),
            agrupado AS (
                SELECT
                    codautora,
                    codlibro,
                    codescena,
                    AVG(numviews)           AS prom_numviews,
                    AVG(numinteractions)    AS prom_interacciones
                FROM filtradas
                GROUP BY codautora, codlibro, codescena
            ),
            maxmintotal AS (
                SELECT
                    MIN(prom_numviews)          AS min_prom_numviews,
                    MAX(prom_numviews)          AS max_prom_numviews,
                    MIN(prom_interacciones)     AS min_prom_interacciones,
                    MAX(prom_interacciones)     AS max_prom_interacciones
                FROM agrupado
            )
            SELECT
                COALESCE(a.nbautora, '') || ' ' || COALESCE(a.apeautora, '') AS Author_name,
                lb.deslibro                                 AS Book,
                e.codescena                                 AS Scene_Code,
                esc.desscena                                AS Scene,
                ROUND(
                    0.5 * CASE 
                        WHEN (m.max_prom_numviews - m.min_prom_numviews) = 0 THEN 0
                        ELSE (e.prom_numviews - m.min_prom_numviews) / (m.max_prom_numviews - m.min_prom_numviews)
                    END
                    +
                    0.5 * CASE
                        WHEN (m.max_prom_interacciones - m.min_prom_interacciones) = 0 THEN 0
                        ELSE (e.prom_interacciones - m.min_prom_interacciones) / (m.max_prom_interacciones - m.min_prom_interacciones)
                    END
                , 2) AS Score_Scene,
                ROUND(e.prom_numviews,0)                             AS PromViews,
                ROUND(e.prom_interacciones,0)                        AS PromInteracciones
              """);
        if (incluirCount) {
            sql.append(",\n COUNT(*) OVER () AS count\n");
        } else {
            sql.append("\n");
        }


        sql.append("""
            FROM agrupado e
            CROSS JOIN maxmintotal m
            LEFT JOIN m_autora a    ON e.codautora = a.codautora
            LEFT JOIN m_escenalibro esc ON e.codescena = esc.codescena
            LEFT JOIN m_libro lb    ON e.codlibro = lb.codlibro
        """);
        if (incluirCount) {
            sql.append(" LIMIT ?");
            parametros.add(limit);
        }
        return jdbc.queryForList(sql.toString(), parametros.toArray());
    }

    public List<Map<String,Object>> reporteConcisoConnectionV2(FiltersRequestDTO request) {
        return reporteConcisoConnectionV2(request, null);
    }

    public List<Map<String,Object>> reporteConcisoConnectionV2(FiltersRequestDTO request, Integer limit) {
        boolean incluirCount = (limit != null && limit > 0);
        String sql = incluirCount
                ? """
            SELECT
              t.*,
              COUNT(*) OVER () AS "count"
            FROM (
          """
                : """
            SELECT
              t.*
            FROM (
          """;

        sql +=  """
        SELECT 
            COALESCE(a.nbautora, 'Not found: N/A') || ' ' || COALESCE(a.apeautora, '') AS "Author name",
            COALESCE(lb.deslibro, 'Not found: N/A') AS "Book name",
            COALESCE(esc.desscena, 'Not found: N/A') AS "Scene name",
            COALESCE(tp.despost, 'Not found: N/A') AS "Post Type",
            mp.fecpublicacion AS "Date posted",
            mp.horapublicacion AS "Time posted",
            mp.nbrcuentatiktok AS "TikTok Username",
            mp.urlpublicacion AS "Post URL",
            mp.numviews AS "Views",
            mp.numlikes AS "Likes",
            mp.numcomments AS "Comments",
            mp.numreposts AS "Reposted",
            mp.numsaves AS "Saves",
            CASE
                        WHEN COALESCE(mp.numlikes, 0) = 0 THEN '0%'
                        ELSE (ROUND((COALESCE(mp.numsaves, 0)::numeric / mp.numlikes::numeric) * 100, 0))::text || '%'
                    END AS "Ratio Saves/Likes",
            ROUND(
                        (
                            COALESCE(mp.numlikes, 0) * 0.8
                          + COALESCE(mp.numsaves, 0) * 1.2
                          + COALESCE(mp.numreposts, 0)
                          + COALESCE(mp.numcomments, 0) / 2.0
                        ) / 4.0
                    ) AS "Best Scenes Score",    
            mp.numengagement AS "Engagement rate",
            mp.numinteractions AS "Interactions",
            mp.deshashtags AS "Hashtags",
            mp.nrohashtag AS "Number of Hashtags",
            mp.urlsounds AS "Sound URL"
        FROM h_metricapublicacion mp
        INNER JOIN (
            SELECT 
                codpublicacion, 
                MAX(mpint.fecreacionregistro+mpint.horacreacionregistro) AS max_fecreacionregistro
            FROM h_metricapublicacion mpint
            WHERE 1=1
        """;

        List<Object> parametros = new ArrayList<>();
        if (!request.getPubStartDate().isEmpty() && !request.getPubFinishtDate().isEmpty()) {
            sql += " AND mpint.fecpublicacion BETWEEN ? AND ?";
            parametros.add(java.sql.Date.valueOf(request.getPubStartDate()));
            parametros.add(java.sql.Date.valueOf(request.getPubFinishtDate()));
        }

        if (!request.getTrackStartDate().isEmpty() && !request.getTrackFinishtDate().isEmpty()) {
            sql += " AND mpint.fecreacionregistro BETWEEN ? AND ?";
            parametros.add(java.sql.Date.valueOf(request.getTrackStartDate()));
            parametros.add(java.sql.Date.valueOf(request.getTrackFinishtDate()));
        }

        if (!request.getAuthorList().isEmpty()) {
            sql += " AND mpint.codautora IN (" + String.join(",", Collections.nCopies(request.getAuthorList().size(), "?")) + ")";
            parametros.addAll(request.getAuthorList());
        }

        if (!request.getBookList().isEmpty()) {
            sql += " AND mpint.codlibro IN (" + String.join(",", Collections.nCopies(request.getBookList().size(), "?")) + ")";
            parametros.addAll(request.getBookList());
        }

        if (!request.getPAList().isEmpty()) {
            sql += " AND mpint.codposteador IN (" + String.join(",", Collections.nCopies(request.getPAList().size(), "?")) + ")";
            parametros.addAll(request.getPAList());
        }

        if (!request.getSceneList().isEmpty()) {
            sql += " AND mpint.codescena IN (" + String.join(",", Collections.nCopies(request.getSceneList().size(), "?")) + ")";
            parametros.addAll(request.getSceneList());
        }

        if (!request.getTypePostList().isEmpty()) {
            sql += " AND mpint.tippublicacion IN (" + String.join(",", Collections.nCopies(request.getTypePostList().size(), "?")) + ")";
            parametros.addAll(request.getTypePostList());
        }

        if (!request.getAccountList().isEmpty()) {
            sql += " AND mpint.nbrcuentatiktok IN (" + String.join(",", Collections.nCopies(request.getAccountList().size(), "?")) + ")";
            parametros.addAll(request.getAccountList());
        }

        if (!request.getPostIDList().isEmpty()) {
            sql += " AND mpint.codpublicacion IN (" + String.join(",", Collections.nCopies(request.getPostIDList().size(), "?")) + ")";
            parametros.addAll(request.getPostIDList());
        }

        if (!request.getRegionList().isEmpty()) {
            sql += " AND mpint.codregionposteo IN (" + String.join(",", Collections.nCopies(request.getRegionList().size(), "?")) + ")";
            parametros.addAll(request.getRegionList());
        }

        sql += """
                GROUP BY mpint.codpublicacion
            ) base
            ON mp.codpublicacion = base.codpublicacion 
            AND (mp.fecreacionregistro+mp.horacreacionregistro) = base.max_fecreacionregistro
            LEFT JOIN m_autora a ON mp.codautora = a.codautora
            LEFT JOIN m_escenalibro esc ON mp.codescena = esc.codescena
            LEFT JOIN m_libro lb ON mp.codlibro = lb.codlibro
            LEFT JOIN m_tipopost tp ON mp.tippublicacion = tp.tippublicacion
            LEFT JOIN m_posteadorasistente po ON mp.codposteador = po.codposteador
            WHERE 1=1
        """;

        if (!request.getLikesMin().isEmpty() || !request.getLikesMax().isEmpty()) {
            sql += " AND mp.numlikes BETWEEN ? AND ?";
            parametros.add(request.getLikesMin().isEmpty() ? 0 : Integer.parseInt(request.getLikesMin()));
            parametros.add(request.getLikesMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getLikesMax()));
        }

        if (!request.getInteractionMin().isEmpty() || !request.getInteractionMax().isEmpty()) {
            sql += " AND mp.numinteractions BETWEEN ? AND ?";
            parametros.add(request.getInteractionMin().isEmpty() ? 0: Integer.parseInt(request.getInteractionMin()));
            parametros.add(request.getInteractionMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getInteractionMax()));
        }

        if (!request.getSavesMin().isEmpty() || !request.getSavesMax().isEmpty()) {
            sql += " AND mp.numsaves BETWEEN ? AND ?";
            parametros.add(request.getSavesMin().isEmpty() ? 0: Integer.parseInt(request.getSavesMin()));
            parametros.add(request.getSavesMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getSavesMax()));
        }

        if (!request.getCommentsMin().isEmpty() || !request.getCommentsMax().isEmpty()) {
            sql += " AND mp.numcomments BETWEEN ? AND ?";
            parametros.add(request.getCommentsMin().isEmpty() ? 0: Integer.parseInt(request.getCommentsMin()));
            parametros.add(request.getCommentsMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getCommentsMax()));
        }

        if (!request.getViewsMin().isEmpty() || !request.getViewsMax().isEmpty()) {
            sql += " AND mp.numviews BETWEEN ? AND ?";
            parametros.add(request.getViewsMin().isEmpty() ? 0: Integer.parseInt(request.getViewsMin()));
            parametros.add(request.getViewsMax().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(request.getViewsMax()));
        }

        if (!request.getEngagementMin().isEmpty() || !request.getEngagementMax().isEmpty()) {
            sql += " AND mp.numengagement BETWEEN ? AND ?";
            parametros.add(request.getEngagementMin().isEmpty() ? 0: Double.parseDouble(request.getEngagementMin()));
            parametros.add(request.getEngagementMax().isEmpty() ? 100 : Double.parseDouble(request.getEngagementMax()));
        }

        // cerrar wrapper
        sql += "\n) t";

        if (incluirCount) {
            sql += " LIMIT ?";
            parametros.add(limit);
        }
        logger.info("Registros dbqueries Reporte conciso obtenido correctamente");
        return jdbc.queryForList(sql,parametros.toArray());
    }

    public List<Map<String,Object>> getTypePosts_bd() {
        String sql = """
        SELECT tippublicacion, despost from m_tipopost
    """;
        return jdbc.queryForList(sql);
    }


}
