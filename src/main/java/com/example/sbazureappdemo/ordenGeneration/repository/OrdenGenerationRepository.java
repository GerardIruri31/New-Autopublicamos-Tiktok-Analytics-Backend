package com.example.sbazureappdemo.ordenGeneration.repository;

import com.example.sbazureappdemo.ordenGeneration.dto.*;
import com.example.sbazureappdemo.orderQueries.dto.QueryResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
@AllArgsConstructor
public class OrdenGenerationRepository {
    private static final Logger logger = LoggerFactory.getLogger(OrdenGenerationRepository.class);
    private final JdbcTemplate jdbc;

    public List<PaResponseDTO> selectPA() {
        String sql = """
            SELECT codposteador,pa_correo,\s
            INITCAP(
                COALESCE(nbposteador, 'NoName') || ' ' || COALESCE(apematposteador, 'NoFirstName')) AS nbrposteador
            from m_posteadorasistente
            where flvigente = 'S'
            order by nbrposteador
        """;
        return jdbc.query(sql, (rs, rowNum) -> {
            PaResponseDTO pa = new PaResponseDTO();
            pa.setCodposteador(rs.getString("codposteador"));
            pa.setNbrposteador(rs.getString("nbrposteador"));
            pa.setPa_correo(rs.getString("pa_correo"));
            return pa;
        });
    }

    public List<TelephoneResponseDTO> selectTelephone(String PaCode) {
        String sql = """
            SELECT codtelefono, 'NOR' AS tiptelefono\s
            from m_posteadortelefono
            where codposteador = ? AND flvigente = 'S'
            UNION
            SELECT codtelefono, tiptelefono from m_telefono
            where tiptelefono IN ('SOP','SOP2')
            AND flvigente = 'S'
        """;
        return jdbc.query(sql, new Object[]{PaCode},(rs,rowNum) -> {
            TelephoneResponseDTO telephone = new TelephoneResponseDTO();
            telephone.setCodtelefono(rs.getString("codtelefono"));
            telephone.setTiptelefono(rs.getString("tiptelefono"));
            return telephone;
        });
    }

    public List<AuthorResponseDTO> selectAuthorByNormalPhone(String codtelefono, String codposteador) {
        String sql = """
            SELECT DISTINCT
                l.codlibro,
                l.deslibro,
                l.codautora,
                INITCAP(COALESCE(a.nbautora, 'NoName') || ' ' || COALESCE(a.apeautora, 'NoFirstName')) AS nbrautora
            FROM m_posteadortelefono pt
            JOIN m_librotelefonocuenta ltc
                ON ltc.codtelefono = pt.codtelefono
            JOIN m_libro l\s
                ON l.codlibro = ltc.codlibro\s
            JOIN m_autora a
                ON a.codautora = l.codautora
            WHERE ltc.codtelefono = ?
            AND pt.codposteador = ?
              AND ltc.flvigente = 'S'
              AND l.flvigente = 'S'
              AND a.flvigente = 'S'
              AND pt.flvigente = 'S'
            order by nbrautora
        """;
        return jdbc.query(sql, new Object[]{codtelefono,codposteador}, (rs, rowNum) -> {
            AuthorResponseDTO dto = new AuthorResponseDTO();
            dto.setCodlibro(rs.getString("codlibro"));
            dto.setDeslibro(rs.getString("deslibro"));
            dto.setCodautora(rs.getString("codautora"));
            dto.setNbrautora(rs.getString("nbrautora"));
            return dto;
        });
    }

    public List<AuthorResponseDTO> selectAuthorByPaWithoutPhone(String codposteador) {
        String sql = """
            SELECT DISTINCT
                l.codlibro,
                l.deslibro,
                l.codautora,
                INITCAP(COALESCE(a.nbautora, 'NoName') || ' ' || COALESCE(a.apeautora, 'NoFirstName')) AS nbrautora
            FROM m_posteadortelefono pt
            JOIN m_librotelefonocuenta ltc
                ON ltc.codtelefono = pt.codtelefono
            JOIN m_libro l
                ON l.codlibro = ltc.codlibro
            JOIN m_autora a
                ON a.codautora = l.codautora
            WHERE pt.codposteador = ?
              AND ltc.flvigente = 'S'
              AND l.flvigente = 'S'
              AND a.flvigente = 'S'
              AND pt.flvigente = 'S'
            order by nbrautora
        """;
        return jdbc.query(sql, new Object[] {codposteador}, (rs, rowNum) -> {
            AuthorResponseDTO dto = new AuthorResponseDTO();
            dto.setCodlibro(rs.getString("codlibro"));
            dto.setDeslibro(rs.getString("deslibro"));
            dto.setCodautora(rs.getString("codautora"));
            dto.setNbrautora(rs.getString("nbrautora"));
            return dto;
        });
    }

    public List<AuthorResponseDTO> selectAuthorBySupportPhone(String codtelefono) {
        String sql = """
            SELECT DISTINCT
                a.codautora,
                INITCAP(COALESCE(a.nbautora, 'NoName') || ' ' || COALESCE(a.apeautora, 'NoFirstName')) AS nbrautora,
                l.codlibro,
                l.deslibro
                FROM m_libro l\s
                JOIN m_autora a ON a.codautora = l.codautora
                WHERE a.flvigente = 'S'
                AND l.flvigente = 'S'
                AND EXISTS (
                    SELECT 1
                    FROM m_telefono t
                    WHERE t.codtelefono = ?
                        AND t.tiptelefono IN ('SOP', 'SOP2')
                        AND t.flvigente = 'S'
                )
            order by nbrautora
        """;
        return jdbc.query(sql, new Object[] {codtelefono}, (rs, rowNum) -> {
            AuthorResponseDTO dto = new AuthorResponseDTO();
            dto.setCodlibro(rs.getString("codlibro"));
            dto.setDeslibro(rs.getString("deslibro"));
            dto.setCodautora(rs.getString("codautora"));
            dto.setNbrautora(rs.getString("nbrautora"));
            return dto;
        });
    }

    public List<PostTypeResponseDTO> selectPostType(String codlibro) {
        String sql = """
            SELECT DISTINCT e.tippublicacion, t.despost
            from m_escenalibro e
            JOIN m_tipopost t ON t.tippublicacion = e.tippublicacion
            where e.codlibro = ?
            AND e.codestadoescena IN (1,2)
        """;
        return jdbc.query(sql,new Object[]{codlibro}, (rs,rowNum) -> {
            PostTypeResponseDTO dto = new PostTypeResponseDTO();
            dto.setTippublicacion(rs.getString("tippublicacion"));
            dto.setDespost(rs.getString("despost"));
            return dto;
        });
    }

    public List<SceneResponseDTO> selectScene(SceneRequestDTO requestDTO) {
        String sql = """
            SELECT e.codescena, e.desscena
            from m_escenalibro e\s
            where e.codlibro = ?
            AND e.tippublicacion = ?     
        """;
        return jdbc.query(sql,new Object[]{requestDTO.getCodlibro(), requestDTO.getTippublicacion()},(rs,rowNum) -> {
            SceneResponseDTO dto = new SceneResponseDTO();
            dto.setCodescena(rs.getString("codescena"));
            dto.setDesscena(rs.getString("desscena"));
            return dto;
        });
    }

    public imagesVideoDTO insertImagesVideo(String tippublicacion, String codusuarioauditoria, String codimagenprincipal, String codimagendialogo, String codimagenscreenshot,String codvideo,String codescena, String codlibro) {
        String sql = "SELECT * FROM public.fn_mant_imagenvideo_ordentrabajo(?,?,?,?,?,?,?,?)";
        return jdbc.queryForObject(sql, (rs, rowNum) -> {
            imagesVideoDTO dto = new imagesVideoDTO();
            dto.setCodimagenprincipal(getIntegerNullable(rs, "o_codimagenprincipal"));
            dto.setCodimagenscreenshot(getIntegerNullable(rs, "o_codimagenscreenshot"));
            dto.setCodimagendialogo(getIntegerNullable(rs, "o_codimagendialogo"));
            dto.setCodvideo(getIntegerNullable(rs, "o_codvideo"));
            dto.setCoderror(rs.getString("o_coderror"));
            dto.setDeserror(rs.getString("o_deserror"));

            return dto;
        }, tippublicacion, codimagenprincipal, codimagenscreenshot, codimagendialogo, codvideo, codusuarioauditoria, codescena, codlibro);
    }


    private Integer getIntegerNullable(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }


    public Long manualOrder(List<String> columnas, List<String> placeholders, List<Object> valores) {
        String sql = "INSERT INTO public.h_ordentrabajo (" +
                String.join(", ", columnas) +
                ") VALUES (" +
                String.join(", ", placeholders) +
                ")";
        return jdbc.queryForObject(sql, Long.class, valores.toArray());
    }

    public void editOrder(Long codordentrabajo, List<String> columnas, List<String> placeholders, List<Object> valores) {
        if (columnas == null || columnas.isEmpty()) {
            return;
        }
        List<String> sets = new ArrayList<>();
        for (int i = 0; i < columnas.size(); i++) {
            sets.add(columnas.get(i) + " = " + placeholders.get(i));
        }
        String sql = """
            UPDATE public.h_ordentrabajo
            SET %s
            WHERE codordentrabajo = ?
            """.formatted(String.join(", ", sets));
        valores.add(codordentrabajo);
        jdbc.update(sql, valores.toArray());
    }


    public void editCompleteOrderFlag(Integer id, Integer flag) {
        String sql = """
           UPDATE h_ordentrabajo
           set codestadoorden = ?
           where codordentrabajo = ?
        """;
        jdbc.update(sql,flag,id);
    }

    public List<ImagesVideosPerTipPublicacionDTO> requiredImagesPerTipPublicacion() {
        String sql = """
                SELECT t.despost as tippublicacion, e.tipimagenvideo 
                FROM m_elementostipoposteo e 
                JOIN m_tipopost t ON t.tippublicacion = e.tippublicacion 
                where e.flvigente = 'S'
                """;
        return jdbc.query(sql, (rs, rowNum) -> {
            ImagesVideosPerTipPublicacionDTO dto = new ImagesVideosPerTipPublicacionDTO();
            dto.setTippublicacion(rs.getString("tippublicacion"));
            dto.setTipimagenvideo(rs.getInt("tipimagenvideo"));
            return dto;
        });
    }

    public AutoGenerationResponseDTO  autoGeneration(FiltersRequestDTO requestDTO) {
        return jdbc.execute((java.sql.Connection con) -> {
            String sql = "{ call public.sp_orquestar_generacion_ordenes_automaticas(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?) }";

            try (CallableStatement cs = con.prepareCall(sql)) {

                // 1. p_correo
                cs.setString(1, requestDTO.getCorreo());

                // 2. p_codposteador
                cs.setString(2, requestDTO.getCodposteador());

                // 3. p_tiptelefono
                cs.setString(3, requestDTO.getTiptelefono());

                // 4. p_codtelefono
                cs.setString(4, requestDTO.getCodtelefono());

                // 5. p_codautora
                cs.setString(5, requestDTO.getCodautora());

                // 6. p_codlibro
                cs.setString(6, requestDTO.getCodlibro());

                // 7. p_tippublicacion
                cs.setString(7, requestDTO.getTippublicacion());

                // 8. p_codescena
                cs.setString(8, requestDTO.getCodescena());

                // 9. p_flgprioridadescena
                cs.setString(9, requestDTO.getFlgprioridadescena());

                // 10. p_flgprioridadsonido
                cs.setString(10, requestDTO.getFlgprioridasonido());

                // 11. p_flgprioridadimagenvideo
                cs.setString(11, requestDTO.getFlgprioridadimagenvideo());

                // 12. p_fecinicioplanposteo
                if (requestDTO.getFecinicioplanposteo() != null) {
                    cs.setDate(12, java.sql.Date.valueOf(requestDTO.getFecinicioplanposteo()));
                } else {
                    cs.setNull(12, Types.DATE);
                }

                // 13. p_fecfinplanposteo
                if (requestDTO.getFecfinplanposteo() != null) {
                    cs.setDate(13, java.sql.Date.valueOf(requestDTO.getFecfinplanposteo()));
                } else {
                    cs.setNull(13, Types.DATE);
                }

                // 14. p_ctdordenesmetamanual
                if (requestDTO.getCtdordenesmetamanual() != null) {
                    cs.setInt(14, requestDTO.getCtdordenesmetamanual());
                } else {
                    cs.setNull(14, Types.INTEGER);
                }

                // 15. p_codcabeceraordentrabajo
                cs.setNull(15, Types.BIGINT);
                cs.registerOutParameter(15, Types.BIGINT);

                // 16. p_var_ctdordenes
                cs.setInt(16, 0);
                cs.registerOutParameter(16, Types.INTEGER);

                // 17. p_var_ctdordenescompleta
                cs.setInt(17, 0);
                cs.registerOutParameter(17, Types.INTEGER);

                // 18. p_var_ctdordenesincompleta
                cs.setInt(18, 0);
                cs.registerOutParameter(18, Types.INTEGER);

                // 19. p_msj_error_log
                cs.setNull(19, Types.VARCHAR);
                cs.registerOutParameter(19, Types.VARCHAR);

                cs.execute();

                long codCabeceraValue = cs.getLong(15);
                Long codCabecera = cs.wasNull() ? null : codCabeceraValue;

                int ctdOrdenesValue = cs.getInt(16);
                Integer ctdOrdenes = cs.wasNull() ? null : ctdOrdenesValue;

                int ctdOrdenesCompletaValue = cs.getInt(17);
                Integer ctdOrdenesCompleta = cs.wasNull() ? null : ctdOrdenesCompletaValue;

                int ctdOrdenesIncompletaValue = cs.getInt(18);
                Integer ctdOrdenesIncompleta = cs.wasNull() ? null : ctdOrdenesIncompletaValue;

                String msj_error_logValue = cs.getString(19);
                String msj_error_log = cs.wasNull() ? null : msj_error_logValue;

                logger.info("codcabecera: {}", codCabecera);
                logger.info("ctdOrdenes: {}", ctdOrdenes);
                logger.info("ctdOrdenesCompleta: {}", ctdOrdenesCompleta);
                logger.info("ctdOrdenesIncompleta: {}", ctdOrdenesIncompleta);
                logger.info("msj_error_log: {}", msj_error_log);

                return new AutoGenerationResponseDTO(codCabecera, ctdOrdenes, ctdOrdenesCompleta, ctdOrdenesIncompleta,msj_error_log);
            }
        });
    }

    public List<OrderGenerationDetailResponseDTO> selectAutoGeneration(Long codcabeceraordentrabajo, Long codordentrabajo) {
        List<Object> parametros = new ArrayList<>();

        String sql = """
        SELECT
            h.codordentrabajo,
            h.codcabeceraordentrabajo,
            h.codautora,
            COALESCE(a.nbautora, 'NoName') || ' ' || COALESCE(a.apeautora, 'NoLastname') AS n_codautora,
            h.codlibro,
            COALESCE(l.deslibro, 'NoBookDescription') AS n_codlibro,
            el.tippublicacion,
            h.codescenaauto,
            h.codescena,
            COALESCE(el.desscena, 'NoSceneDescription') AS n_codescena,
            h.codposteador,
            COALESCE(pa.nbposteador, 'NoName') || ' ' || COALESCE(pa.apematposteador, 'NoLastname') AS n_codposteador,
            h.codtelefono,
            h.codcuentatiktok,
            h.codsonido,
            COALESCE(s.urlsonido, 'NoSoundDescription') AS n_codsonido,
            h.desscenahook,
            h.descaption,
            h.destropo,
            h.desslide1keywordshide,
            h.desslide2keywordshide,
            h.deshashtag,
            h.despalote,
            h.codimagenprincipal,
            COALESCE(iv_principal.urlimagenvideo, 'NoCodimagenprincipalDescription') AS n_codimagenprincipal,
            h.codimagenscreenshot,
            COALESCE(iv_screenshot.urlimagenvideo, 'NoCodimagenscreenshotDescription') AS n_codimagenscreenshot,
            h.codimagendialogo,
            COALESCE(iv_dialogo.urlimagenvideo, 'NoCodimagendialogoDescription') AS n_codimagendialogo,
            h.codvideo,
            COALESCE(iv_video.urlimagenvideo, 'NoCodvideoDescription') AS n_codvideo,
            h.desinstrucciones,
            h.fecplanposteo,
            case h.codestadoorden 
                when 1 then 'Assigned'
                when 2 then 'Flagged'
                when 3 then '-100 views'
                when 4 then 'Posted'
            END AS codestadoorden,
            h.tipregistroorden,
            h.flgordencompleta,
            h.ctddatoobligincompleto,
            h.desdatoobligincompleto,
            h.deslogerrororden,
            h.codusuarioauditoriacreareg,
            h.codusuarioauditoriaactualizareg,
            h.fecreacionregistro,
            h.horacreacionregistro,
            h.fecactualizacionregistro,
            h.horaactualizacionregistro
        FROM h_ordentrabajo h
        LEFT JOIN m_autora a
            ON a.codautora = h.codautora
        LEFT JOIN m_libro l
            ON l.codlibro = h.codlibro
        LEFT JOIN m_escenalibro el
            ON el.codescena = h.codescena
        LEFT JOIN m_posteadorasistente pa
            ON pa.codposteador = h.codposteador
        LEFT JOIN m_sonido s
            ON s.codsonido = h.codsonido
        LEFT JOIN m_imagenvideo iv_principal
            ON iv_principal.codimagenvideo = h.codimagenprincipal
        LEFT JOIN m_imagenvideo iv_screenshot
            ON iv_screenshot.codimagenvideo = h.codimagenscreenshot
        LEFT JOIN m_imagenvideo iv_dialogo
            ON iv_dialogo.codimagenvideo = h.codimagendialogo
        LEFT JOIN m_imagenvideo iv_video
            ON iv_video.codimagenvideo = h.codvideo
        """;

        if (codordentrabajo != null && codcabeceraordentrabajo == null) {
            sql += """
            WHERE h.codordentrabajo = ?
            """;
            parametros.add(codordentrabajo);
        } else if (codcabeceraordentrabajo != null && codordentrabajo == null) {
            sql += """
            WHERE h.codcabeceraordentrabajo = ?
            """;
            parametros.add(codcabeceraordentrabajo);
        } else {
            throw new IllegalArgumentException("Debe enviar codordentrabajo o codcabeceraordentrabajo");
        }

        return jdbc.query(sql, parametros.toArray(), (rs, rowNum) -> {
            OrderGenerationDetailResponseDTO dto = new OrderGenerationDetailResponseDTO();

            dto.setCodordentrabajo(getLongNullable(rs, "codordentrabajo"));
            dto.setCodcabeceraordentrabajo(getLongNullable(rs, "codcabeceraordentrabajo"));

            dto.setCodautora(rs.getString("codautora"));
            dto.setNCodautora(rs.getString("n_codautora"));

            dto.setCodlibro(rs.getString("codlibro"));
            dto.setNCodlibro(rs.getString("n_codlibro"));

            dto.setTippublicacion(rs.getString("tippublicacion"));

            dto.setCodescenaauto(rs.getString("codescenaauto"));
            dto.setCodescena(rs.getString("codescena"));
            dto.setNCodescena(rs.getString("n_codescena"));

            dto.setCodposteador(rs.getString("codposteador"));
            dto.setNCodposteador(rs.getString("n_codposteador"));

            dto.setCodtelefono(rs.getString("codtelefono"));
            dto.setCodcuentatiktok(rs.getString("codcuentatiktok"));

            dto.setCodsonido(rs.getString("codsonido"));
            dto.setNCodsonido(rs.getString("n_codsonido"));

            dto.setDesscenahook(rs.getString("desscenahook"));
            dto.setDescaption(rs.getString("descaption"));
            dto.setDestropo(rs.getString("destropo"));
            dto.setDesslide1keywordshide(rs.getString("desslide1keywordshide"));
            dto.setDesslide2keywordshide(rs.getString("desslide2keywordshide"));
            dto.setDeshashtag(rs.getString("deshashtag"));
            dto.setDespalote(rs.getString("despalote"));

            dto.setCodimagenprincipal(getLongNullable(rs, "codimagenprincipal"));
            dto.setNCodimagenprincipal(rs.getString("n_codimagenprincipal"));

            dto.setCodimagenscreenshot(getLongNullable(rs, "codimagenscreenshot"));
            dto.setNCodimagenscreenshot(rs.getString("n_codimagenscreenshot"));

            dto.setCodimagendialogo(getLongNullable(rs, "codimagendialogo"));
            dto.setNCodimagendialogo(rs.getString("n_codimagendialogo"));

            dto.setCodvideo(getLongNullable(rs, "codvideo"));
            dto.setNCodvideo(rs.getString("n_codvideo"));

            dto.setDesinstrucciones(rs.getString("desinstrucciones"));
            dto.setFecplanposteo(rs.getDate("fecplanposteo"));

            dto.setCodestadoorden(rs.getString("codestadoorden"));
            dto.setTipregistroorden(rs.getString("tipregistroorden"));
            dto.setFlgordencompleta(rs.getString("flgordencompleta"));

            dto.setCtddatoobligincompleto((Integer) rs.getObject("ctddatoobligincompleto"));
            dto.setDesdatoobligincompleto(rs.getString("desdatoobligincompleto"));
            dto.setDeslogerrororden(rs.getString("deslogerrororden"));

            dto.setCodusuarioauditoriacreareg(rs.getString("codusuarioauditoriacreareg"));
            dto.setCodusuarioauditoriaactualizareg(rs.getString("codusuarioauditoriaactualizareg"));

            dto.setFecreacionregistro(rs.getDate("fecreacionregistro"));
            dto.setHoracreacionregistro(rs.getTime("horacreacionregistro"));
            dto.setFecactualizacionregistro(rs.getDate("fecactualizacionregistro"));
            dto.setHoraactualizacionregistro(rs.getTime("horaactualizacionregistro"));
            return dto;
        });
    }

    private Long getLongNullable(ResultSet rs, String column) throws SQLException {
        Number value = (Number) rs.getObject(column);
        return value != null ? value.longValue() : null;
    }

}
