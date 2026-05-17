package com.example.sbazureappdemo.user.repository;

import com.example.sbazureappdemo.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbc;
    public UserDTO getAllModules(String email) {
        String sql = """
                SELECT r.codmoduloapp
                FROM m_usuariorol u
                JOIN m_relacionrolmoduloapp r ON u.tiprol = r.tiprol
                where lower(u.codusuario) = lower(?);
                """;
        List<String> modules = jdbc.queryForList(sql,String.class,email);
        UserDTO dto = new UserDTO();
        dto.setModules(modules);
        return dto;
    }

    public String findTiprolByCodusuario(String codusuario) {
        String sql = """
            SELECT tiprol
            FROM m_usuariorol
            WHERE LOWER(codusuario) = LOWER(?)
        """;
        List<String> roles = jdbc.query(sql,new Object[]{codusuario},
                (rs, rowNum) -> rs.getString("tiprol")
        );
        return roles.isEmpty() ? null : roles.get(0);
    }

}
