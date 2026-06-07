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
            int nCodsonido = rs.getInt("nCodsonido");
            dto.setNCodsonido(rs.wasNull() ? null : nCodsonido);

            dto.setDesscenahook(rs.getString("desscenahook"));
            dto.setDescaption(rs.getString("descaption"));
            dto.setDestropo(rs.getString("destropo"));
            dto.setDesslide1keywordshide(rs.getString("desslide1keywordshide"));
            dto.setDesslide2keywordshide(rs.getString("desslide2keywordshide"));
            dto.setDeshashtag(rs.getString("deshashtag"));
            dto.setDespalote(rs.getString("despalote"));

            dto.setCodimagenprincipal(rs.getString("codimagenprincipal"));
            int nCodimagenprincipal = rs.getInt("nCodimagenprincipal");
            dto.setNCodimagenprincipal(rs.wasNull() ? null : nCodimagenprincipal);

            dto.setCodimagenscreenshot(rs.getString("codimagenscreenshot"));
            int nCodimagenscreenshot = rs.getInt("nCodimagenscreenshot");
            dto.setNCodimagenscreenshot(rs.wasNull() ? null : nCodimagenscreenshot);

            dto.setCodimagendialogo(rs.getString("codimagendialogo"));
            int nCodimagendialogo = rs.getInt("nCodimagendialogo");
            dto.setNCodimagendialogo(rs.wasNull() ? null : nCodimagendialogo);

            dto.setCodvideo(rs.getString("codvideo"));
            int nCodvideo = rs.getInt("nCodvideo");
            dto.setNCodvideo(rs.wasNull() ? null : nCodvideo);


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
            System.out.println(dto);
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
