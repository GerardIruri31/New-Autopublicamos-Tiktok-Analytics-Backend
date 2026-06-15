package com.example.sbazureappdemo.dataMaintenance.repository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class DataMaintenanceRepository {
    Logger logger = LoggerFactory.getLogger(DataMaintenanceRepository.class);
    private final JdbcTemplate jdbc;

    public Integer uploadConnection(String tableName, Map<String,Object> response,String finalTableName, String pkName, String fkSql,String userId) {

        String ddl = switchCreateTable(tableName,finalTableName,pkName,fkSql);
        jdbc.execute(ddl);

        String insertSql = switchInsertTable(tableName, finalTableName);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> records = (List<Map<String, Object>>) response.getOrDefault("records", Collections.emptyList());

        jdbc.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
            @Override public void setValues(PreparedStatement ps, int i) throws SQLException {
                Map<String,Object> r= records.get(i);
                switch (tableName) {
                    case "m_escenaimagenvideo":
                        ps.setObject(1, r.get("codescenasinversion"));
                        ps.setObject(2, r.get("numversion"));
                        ps.setObject(3, r.get("tipimagenvideo"));
                        ps.setObject(4, r.get("codimagenvideo"));
                        ps.setObject(5, r.get("urlimagenvideo"));
                        ps.setObject(6, r.get("flvigente"));
                        ps.setObject(7, userId);
                        break;
                    case "m_librotipopostimagenvideo":
                        ps.setObject(1, r.get("codlibro"));
                        ps.setObject(2, r.get("tippublicacion"));
                        ps.setObject(3, r.get("tipimagenvideo"));
                        ps.setObject(4, r.get("codimagenvideo"));
                        ps.setObject(5, r.get("urlimagenvideo"));
                        ps.setObject(6, r.get("flgprioridad"));
                        ps.setObject(7, r.get("flvigente"));
                        ps.setObject(8, userId);
                        break;
                    case "m_librotelefonocuenta":
                        ps.setObject(1, r.get("codlibro"));
                        ps.setObject(2, r.get("codtelefono"));
                        ps.setObject(3, r.get("codcuenta"));
                        ps.setObject(4, r.get("flvigente"));
                        ps.setObject(5, userId);
                        break;
                    default:
                        throw new IllegalArgumentException("Tabla no soportada en batch setter: " + tableName);
                }
            }
            @Override public int getBatchSize() {
                return records.size();
            }
        });

        // devolver num elementos insertados en tb temporal
        return jdbc.queryForObject("SELECT COUNT(*) FROM " + finalTableName, Integer.class);
    }

    public Map<String,Object> ejecutarProcesoStaging(String tableName, String finalTableName, String userId) {
        String sql;
        switch (tableName) {
            case "m_escenaimagenvideo":
                sql = "SELECT * FROM public.fn_procesar_escenaimagenvideo_staging(?, ?)";
                break;
            case "m_librotipopostimagenvideo":
                sql = "SELECT * FROM public.fn_procesar_librotipopostimagenvideo_staging(?, ?)";
                break;
            case "m_librotelefonocuenta":
                sql = "SELECT * FROM public.fn_procesar_librotelefonocuenta(?,?)";
                break;
            default:
                throw new IllegalArgumentException("Tabla no soportada para proceso staging: " + tableName);
        }
        Map<String, Object> result = jdbc.queryForMap(sql, finalTableName, userId);
        Object codError = result.get("o_coderror");
        Object msjError = result.get("o_msjerror");
        if (codError != null && !codError.toString().isBlank()) {
            throw new IllegalArgumentException(msjError != null ? msjError.toString() : codError.toString());
        }
        return result;
    }

    private String switchCreateTable(String tableName, String finalTableName, String pkName, String fkSql) {
        String ddl;
        switch (tableName) {
            case "m_escenaimagenvideo":
                ddl = String.format("""
            CREATE TABLE %s (
              codescenasinversion varchar NOT NULL,
              numversion int NOT NULL,
              tipimagenvideo int NOT NULL,
              codimagenvideo int,
              urlimagenvideo varchar NOT NULL,
              flvigente char(1) NOT NULL,
              codusuarioauditoria varchar,
              fecreacionregistro date,
              horacreacionregistro time,
              fecactualizacionregistro date,
              horaactualizacionregistro time,
              CONSTRAINT %s PRIMARY KEY (codescenasinversion, numversion, tipimagenvideo)%s
            );
            """, finalTableName, pkName, fkSql);
                break;

            case "m_librotipopostimagenvideo":
                ddl = String.format("""
            CREATE TABLE %s (
              codlibro varchar NOT NULL,
              tippublicacion varchar NOT NULL,
              tipimagenvideo int NOT NULL,
              codimagenvideo int,
              urlimagenvideo varchar NOT NULL,
              flgprioridad char(1),
              flvigente char(1) NOT NULL,
              codusuarioauditoria varchar,
              fecreacionregistro date,
              horacreacionregistro time,
              fecactualizacionregistro date,
              horaactualizacionregistro time,
              CONSTRAINT %s PRIMARY KEY (codlibro, tippublicacion, tipimagenvideo,urlimagenvideo)%s
            );
            """, finalTableName, pkName, fkSql);
                break;
            case "m_librotelefonocuenta":
                ddl = String.format("""
            CREATE TABLE %s (
              codlibro varchar NOT NULL,
              codtelefono varchar NOT NULL,
              codcuenta varchar NOT NULL,
              flvigente char(1) NOT NULL,
              codusuarioauditoria varchar,
              fecreacionregistro date,
              horacreacionregistro time,
              fecactualizacionregistro date,
              horaactualizacionregistro time
              %s
            );""", finalTableName,fkSql);
                break;
            default:
                throw new IllegalArgumentException("Tabla no soportada para staging: " + tableName);
        }
        return ddl;
    }

    private String switchInsertTable(String tableName, String finalTableName) {
        String insertSql;
        switch (tableName) {
            case "m_escenaimagenvideo":
                insertSql = String.format("""
            INSERT INTO %s (
              codescenasinversion, numversion, tipimagenvideo, codimagenvideo,
              urlimagenvideo, flvigente,
              codusuarioauditoria,
              fecreacionregistro, horacreacionregistro,
              fecactualizacionregistro, horaactualizacionregistro
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_DATE, LOCALTIME, CURRENT_DATE, LOCALTIME)
            ON CONFLICT (codescenasinversion, numversion, tipimagenvideo)
            DO UPDATE SET
              urlimagenvideo = EXCLUDED.urlimagenvideo,
              flvigente = EXCLUDED.flvigente,
              codusuarioauditoria = EXCLUDED.codusuarioauditoria,
              fecactualizacionregistro = CURRENT_DATE,
              horaactualizacionregistro = LOCALTIME;
            """, finalTableName);
                break;

            case "m_librotipopostimagenvideo":
                insertSql = String.format("""
            INSERT INTO %s (
              codlibro, tippublicacion, tipimagenvideo, codimagenvideo,
              urlimagenvideo, flgprioridad, flvigente,
              codusuarioauditoria,
              fecreacionregistro, horacreacionregistro,
              fecactualizacionregistro, horaactualizacionregistro
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_DATE, LOCALTIME, CURRENT_DATE, LOCALTIME)
            ON CONFLICT (codlibro, tippublicacion, tipimagenvideo,urlimagenvideo)
            DO NOTHING;
            """, finalTableName);
                break;
            case "m_librotelefonocuenta" :
                insertSql = String.format("""
            INSERT INTO %s (
              codlibro, codtelefono, codcuenta, flvigente,
              codusuarioauditoria,
              fecreacionregistro, horacreacionregistro,
              fecactualizacionregistro, horaactualizacionregistro
            )
            VALUES (?, ?, ?, ?, ?, CURRENT_DATE, LOCALTIME, CURRENT_DATE, LOCALTIME);
            
            """, finalTableName);
                break;
            default:
                throw new IllegalArgumentException("Tabla no soportada para insert staging: " + tableName);
        }
        return insertSql;
    }

    public List<Map<String,Object>> showDBRecordsConnectionPreview(Object tableName, int limit) {
        String sql =
                "SELECT t.*, COUNT(*) OVER () AS count " +
                        "FROM (SELECT * FROM " + tableName + ") t " +
                        "LIMIT ?";
        return jdbc.queryForList(sql, limit);
    }

    public List<Map<String,Object>> showDBRecordsConnection(Object tableName) {
        String sql = "SELECT * FROM " + tableName;
        return jdbc.queryForList(sql);

    }

    public Map<String,Object> uploadRecordsExcelFileConnection(Map<String,Object> response, String userId) {
        // Obtener nombres de las columnas de forma dinámica
            logger.info("Iniciando guardado de datos del Excel importado - DataMaintenance");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> records = (List<Map<String, Object>>) response.getOrDefault("records", Collections.emptyList());
            @SuppressWarnings("unchecked")
            List<String> conflictKeys = (List<String>) response.getOrDefault("conflict", Collections.emptyList());
            if (records.isEmpty()) {
                throw new IllegalArgumentException("No records to process");
            }

            for (Map<String, Object> record : records) {
                record.put("codusuarioauditoria", userId);
            }
            Set<String> columnNames = new LinkedHashSet<>(records.get(0).keySet());
            String tableName = (String) response.getOrDefault("table", "tabla_default");


            // Agregar las columnas obligatorias con timestamps de zona horaria fija
            columnNames.add("fecreacionregistro");
            columnNames.add("horacreacionregistro");
            columnNames.add("fecactualizacionregistro");
            columnNames.add("horaactualizacionregistro");
            String columns = String.join(", ", columnNames);
            String placeholders = columnNames.stream()
                    .map(col -> {
                        if (col.equals("fecreacionregistro") || col.equals("fecactualizacionregistro")) {
                            return "CURRENT_DATE"; // Usa CURRENT_DATE para fechas
                        } else if (col.equals("horacreacionregistro") || col.equals("horaactualizacionregistro")) {
                            return "CURRENT_TIME AT TIME ZONE 'America/Lima'"; // Usa CURRENT_TIME AT TIME ZONE 'America/Lima'
                        } else if (col.equalsIgnoreCase("codmes")) {   //  CAMBIO
                            return "INITCAP(?)"; }
                        else {
                            return "?"; // Para las demás columnas usa valores dinámicos
                        }
                    }).collect(Collectors.joining(", "));



            // Construcción de `ON CONFLICT` dinámico
            String conflictClause = conflictKeys.isEmpty() ? "" :
                    "ON CONFLICT (" + String.join(", ", conflictKeys) + ") DO UPDATE SET " + columnNames.stream().filter(col -> !col.equals("fecreacionregistro") && !col.equals("horacreacionregistro")) // Excluir estos campos
                        .map(col -> {
                            if (col.equals("fecactualizacionregistro")) {
                                return col + " = CURRENT_DATE"; // Actualiza con la fecha actual
                            } else if (col.equals("horaactualizacionregistro")) {
                                return col + " = CURRENT_TIME AT TIME ZONE 'America/Lima'"; // Actualiza con la hora actual
                            } else if (col.equals("codmes")) {
                                return col + " = initcap(EXCLUDED." + col + ")"; // ✅ aplica INITCAP solo a codmes
                            } else {
                                return col + " = EXCLUDED." + col; // Asegura que las claves de conflicto también se actualicen
                            }
                        })
                        .collect(Collectors.joining(", "));



            // Construcción de la consulta final
            String sql = String.format("""
                INSERT INTO %s (%s) VALUES (%s)
                %s;
            """, tableName, columns, placeholders, conflictClause);



            jdbc.batchUpdate(sql, records, records.size(), (ps, record) -> {
                int index = 1;
                for (String col : columnNames) {
                    if (!col.equals("fecreacionregistro") && !col.equals("horacreacionregistro") && !col.equals("fecactualizacionregistro") && !col.equals("horaactualizacionregistro")) {
                        Object value = record.getOrDefault(col, null);
                            if (col.equalsIgnoreCase("fecfinperiodometa") || col.equalsIgnoreCase("fecinicioperiodometa")) {
                                try {
                                    if (value == null || value.toString().trim().isEmpty()) {
                                        ps.setNull(index, java.sql.Types.DATE); // Asignar NULL si el valor es vacío
                                    } else if (value instanceof String) {
                                        // Si es String, parsear al formato correcto
                                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                                        java.util.Date fecha = sdf.parse((String) value);
                                        ps.setDate(index, new java.sql.Date(fecha.getTime()));
                                    } else if (value instanceof java.util.Date) {
                                        // Si ya es Date, convertirlo a java.sql.Date
                                        ps.setDate(index, new java.sql.Date(((java.util.Date) value).getTime()));
                                    } else if (value instanceof LocalDate) {
                                        // Si es LocalDate, convertirlo directamente
                                        ps.setDate(index, java.sql.Date.valueOf((LocalDate) value));
                                    } else {
                                        ps.setNull(index, java.sql.Types.DATE); // Si el formato es incorrecto, asignar NULL
                                    }
                                } catch (ParseException e) {
                                    throw new IllegalArgumentException("Fecha inválida: ", e);
                                }
                            }
                            else {
                                    ps.setObject(index, value);
                                }

                        index++;
                    }
                }
            });
            logger.info("Datos del Excel guardados correctamente en la BD - DataMaintenance");
            return Map.of("message", ((List<?>) response.get("records")).size());
    }

}
