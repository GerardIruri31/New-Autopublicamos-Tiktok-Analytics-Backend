package com.example.sbazureappdemo.dataMaintenance.service;


import com.example.sbazureappdemo.dataMaintenance.repository.DataMaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service
@RequiredArgsConstructor
public class UploadTempTableService {
    private final DataMaintenanceRepository dataMaintenanceRepository;
    private final StagingCleanupService stagingCleanupService;
    Logger logger = LoggerFactory.getLogger(UploadTempTableService.class);


    public Map<String, Object> uploadStaging(Map<String, Object> response, String userId) {
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> records = (List<Map<String,Object>>)  response.getOrDefault("records",Collections.emptyList());
        if (records.isEmpty()) throw new IllegalArgumentException("No records to process");;

        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase(Locale.ROOT);
        String tableName = Objects.toString(response.get("table"),"");
        String finalTableName = tableName + "_" + uuid;
        String shortId = uuid.substring(0, 10);

        String pkName = "pk_" + finalTableName;

        List<List<String>> fkContent = selectFks(tableName, shortId);
        List<String> fks = fkContent.get(0);
        List<String> columns = fkContent.get(1);
        List<String> refs = fkContent.get(2);
        String fkSql = buildFkConstraintsSql(fks,columns,refs);

        try {
            Integer inserted = dataMaintenanceRepository.uploadConnection(tableName ,response, finalTableName, pkName, fkSql, userId);
            if (inserted == 0) throw new IllegalArgumentException("No records to process");
            Map<String, Object> result = dataMaintenanceRepository.ejecutarProcesoStaging(tableName, finalTableName, userId);
            //return Map.of("message", "OK","nuevasUrls", result.get("nuevas_urls"),"existentesUrls", result.get("existentes_urls"),"insertadosEscena", result.get("insertados_escena"));
            return Map.of("message", ((List<?>) response.get("records")).size());
        } catch (Exception e) {
            logger.error("Error en uploadStaging. table={}, staging={}", tableName, finalTableName, e);
            throw new RuntimeException("Error en uploadStaging. table=" + tableName + ", staging=" + finalTableName, e);
        } finally {
            stagingCleanupService.dropTable(finalTableName);
        }
    }



    private String buildFkConstraintsSql(List<String> fks, List<String> columns, List<String> refs) {
        if (fks == null || fks.isEmpty()) return "";
        if (columns == null || columns.size() != fks.size()) throw new IllegalArgumentException("columns debe tener el mismo tamaño que fks");
        if (refs == null || refs.size() != fks.size()) throw new IllegalArgumentException("refs debe tener el mismo tamaño que fks");
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<fks.size();i++) {
            String fkName = fks.get(i);
            String columnName = columns.get(i);
            String refName = refs.get(i);
            sb.append(",\n  CONSTRAINT ")
                    .append(fkName)
                    .append(" FOREIGN KEY (")
                    .append(columnName)
                    .append(") REFERENCES ")
                    .append(refName)
                    .append(" ON DELETE RESTRICT")
                    .append(" ON UPDATE RESTRICT");
        }
        return sb.toString();
    }

    private List<List<String>> selectFks(String tableName, String shortId) {
        List<List<String>> wrapper = new ArrayList<>();
        List<String> fk = new ArrayList<>();
        List<String> columns = new ArrayList<>();
        List<String> refs = new ArrayList<>();
        switch (tableName) {
            case "m_escenaimagenvideo":
                //fk.add("fk_" + tableName + "_" +  shortId + "_e");
                //columns.add("codescenasinversion, numversion");
                //refs.add("public.m_escenalibro(codescenasinversion,numversion)");
                break;
            case "m_librotipopostimagenvideo":
                fk.add("fk_" + tableName + "_" + shortId + "_c");
                columns.add("codlibro");
                refs.add("public.m_libro(codlibro)");
                fk.add("fk_" + tableName + "_" + shortId + "_p");
                columns.add("tippublicacion");
                refs.add("public.m_tipopost(tippublicacion)");
                break;
            default:
                break;
        }
        wrapper.add(fk);
        wrapper.add(columns);
        wrapper.add(refs);
        return wrapper;
    }
}
