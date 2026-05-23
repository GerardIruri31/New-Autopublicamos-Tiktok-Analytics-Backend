package com.example.sbazureappdemo.orderQueries.repository;

import com.example.sbazureappdemo.ordenGeneration.dto.PaResponseDTO;
import com.example.sbazureappdemo.orderQueries.dto.CreatedByDTO;
import com.example.sbazureappdemo.orderQueries.dto.QueryResponse;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;


@Repository
@AllArgsConstructor
public class OrderQueriesRepository {
    private final JdbcTemplate jdbc;


    public List<QueryResponse> search(StringBuilder sql, List<Object> params) {
        return jdbc.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
            QueryResponse dto = new QueryResponse();

            dto.setCodordentrabajo(rs.getInt("codordentrabajo"));
            if (rs.wasNull()) dto.setCodordentrabajo(null);

            dto.setCodcabeceraordentrabajo(rs.getInt("codcabeceraordentrabajo"));
            if (rs.wasNull()) dto.setCodcabeceraordentrabajo(null);

            dto.setCodautora(rs.getString("codautora"));
            dto.setCodlibro(rs.getString("codlibro"));
            dto.setNCodlibro(rs.getString("nCodlibro"));
            dto.setTippublicacion(rs.getString("tippublicacion"));
            dto.setNTippublicacion(rs.getString("nTippublicacion"));

            dto.setCodescena(rs.getString("codescena"));
            dto.setNCodescena(rs.getString("nCodescena"));
            dto.setCodposteador(rs.getString("codposteador"));
            dto.setCodtelefono(rs.getString("codtelefono"));
            dto.setCodcuentatiktok(rs.getString("codcuentatiktok"));
            dto.setCodsonido(rs.getString("codsonido"));

            dto.setDesscenahook(rs.getString("desscenahook"));
            dto.setDescaption(rs.getString("descaption"));
            dto.setDestropo(rs.getString("destropo"));
            dto.setDesslide1keywordshide(rs.getString("desslide1keywordshide"));
            dto.setDesslide2keywordshide(rs.getString("desslide2keywordshide"));
            dto.setDeshashtag(rs.getString("deshashtag"));
            dto.setDespalote(rs.getString("despalote"));

            dto.setCodimagenprincipal(rs.getString("codimagenprincipal"));
            dto.setCodimagenscreenshot(rs.getString("codimagenscreenshot"));
            dto.setCodimagendialogo(rs.getString("codimagendialogo"));
            dto.setCodvideo(rs.getString("codvideo"));

            dto.setDesinstrucciones(rs.getString("desinstrucciones"));

            Date fecplanposteo = rs.getDate("fecplanposteo");
            dto.setFecplanposteo(fecplanposteo != null ? fecplanposteo.toLocalDate() : null);



            dto.setCodestadoorden(rs.getString("codestadoorden"));
            dto.setTipregistroorden(rs.getString("tipregistroorden"));
            dto.setFlgordencompleta(rs.getString("flgordencompleta"));

            dto.setCtddatoobligincompleto(rs.getInt("ctddatoobligincompleto"));
            if (rs.wasNull()) dto.setCtddatoobligincompleto(null);

            dto.setDesdatoobligincompleto(rs.getString("desdatoobligincompleto"));
            dto.setDeslogerrororden(rs.getString("deslogerrororden"));

            dto.setCodusuarioauditoriacreareg(rs.getString("codusuarioauditoriacreareg"));
            dto.setCodusuarioauditoriaactualizareg(rs.getString("codusuarioauditoriaactualizareg"));

            Date fecreacionregistro = rs.getDate("fecreacionregistro");
            dto.setFecreacionregistro(fecreacionregistro != null ? fecreacionregistro.toLocalDate() : null);

            Time horacreacionregistro = rs.getTime("horacreacionregistro");
            dto.setHoracreacionregistro(horacreacionregistro != null ? horacreacionregistro.toLocalTime() : null);

            Date fecactualizacionregistro = rs.getDate("fecactualizacionregistro");
            dto.setFecactualizacionregistro(fecactualizacionregistro != null ? fecactualizacionregistro.toLocalDate() : null);

            Time horaactualizacionregistro = rs.getTime("horaactualizacionregistro");
            dto.setHoraactualizacionregistro(horaactualizacionregistro != null ? horaactualizacionregistro.toLocalTime() : null);

            return dto;
        });
    }

    public List<CreatedByDTO> createdBy() {
        String sql = """
                SELECT codusuario AS codusuarioauditoriacreareg, (COALESCE(nbusuario,'NoName') || ' ' || COALESCE(apepatusuario, 'NoLastName') || ' ' || COALESCE(apematusuario,'NoSecondName')) AS nbrusuarioauditoriacreareg
                from m_usuariorol
                where tiprol IN ('adm','sup') AND flvigente = 'S'
                """;
        return jdbc.query(sql,(rs,rowNumber) -> {
            CreatedByDTO dto = new CreatedByDTO();
            dto.setCodusuarioauditoriacreareg(rs.getString("codusuarioauditoriacreareg"));
            dto.setNbrusuarioauditoriacreareg(rs.getString("nbrusuarioauditoriacreareg"));
            return dto;
        });

    }

}
