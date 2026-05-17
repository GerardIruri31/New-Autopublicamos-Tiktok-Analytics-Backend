package com.example.sbazureappdemo.dbQueries.service;
import java.util.*;

import com.example.sbazureappdemo.dbQueries.dto.FiltersRequestDTO;
import com.example.sbazureappdemo.dbQueries.repository.DbQueryRepository;
import com.example.sbazureappdemo.excelService.ExcelService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DbQueryService {
    Logger logger = LoggerFactory.getLogger(DbQueryService.class);
    private final DbQueryRepository dbQueryRepository;
    private final ExcelService excelService;

    public List<Map<String,Object>> filtrarDatosPreview(FiltersRequestDTO request, int limit) {
        return dbQueryRepository.FilterConnection(request, limit);
    }

    public List<Map<String,Object>> scoreScenesServicePreview(FiltersRequestDTO request, int limit)  {
        return dbQueryRepository.scoreSceneConnectionV2(request, limit);
    }

    public List<Map<String,Object>> reporteConcisoServicePreview(FiltersRequestDTO request, int limit)  {
        return dbQueryRepository.reporteConcisoConnectionV2(request, limit);
    }

    public List<Map<String,Object>>getTypePosts() {
        logger.info("Buscando typePost para dbqueries");
        return dbQueryRepository.getTypePosts_bd();
    }

    public byte[] switchDownloadButton(String reportType, FiltersRequestDTO request) {
        List<Map<String, Object>> data = switch (reportType.toLowerCase()) {
            case "filter" -> dbQueryRepository.FilterConnection(request);
            case "scorescene" -> dbQueryRepository.scoreSceneConnectionV2(request);
            case "conciso" -> dbQueryRepository.reporteConcisoConnectionV2(request);
            default -> throw new IllegalArgumentException("reportType inválido");
        };
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("No hay datos para exportar a Excel");
        }
        return excelService.downloadExcel(data);
    }




}
