package com.example.sbazureappdemo.selectAuthorCodes.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AuthorCodesRepository {
    private final JdbcTemplate jdbc;

    public List<Map<String, Object>> findCodautorasByCorreo(String correo) {
        String sql = """
        SELECT codautora, TRIM(COALESCE(nbautora, '') || ' ' || COALESCE(apeautora, '')) AS nombre_completo
        FROM m_autora
        WHERE lower(autora_correo) = lower(?)
    """;
        return jdbc.queryForList(sql, correo);
    }


    public List<Map<String, Object>> findLibrosByCorreo(String correo) {
        String sql = """
        SELECT l.codlibro,l.deslibro
        FROM m_libro l
        JOIN m_autora a ON a.codautora = l.codautora
        WHERE lower(a.autora_correo) = lower(?)
        ORDER BY l.codlibro
    """;
        return jdbc.queryForList(sql,correo);
    }


    public List<Map<String, Object>> findSeudonimoLibro(String correo) {
        String sql = """
        SELECT l.codlibro,l.deslibro,l.codautora,TRIM(COALESCE(a.nbautora, '') || ' ' || COALESCE(a.apeautora, '')) AS nombre_completo
        FROM m_libro l
        JOIN m_autora a ON a.codautora = l.codautora
        WHERE lower(a.autora_correo) = lower(?)
    """;
        return jdbc.queryForList(sql,correo);
    }
}
