package com.example.sbazureappdemo.apifycall.service;


import com.example.sbazureappdemo.excelService.ExcelService;
import com.example.sbazureappdemo.apifycall.dto.DownloadExcelFiltersRequest;
import com.example.sbazureappdemo.apifycall.repository.ApifyRepository;
import com.example.sbazureappdemo.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ApifyDownloadExcelService {
    Logger logger = LoggerFactory.getLogger(ApifyDownloadExcelService.class);
    private final ExcelService excelService;
    private final ApifyRepository apifyRepository;


    public byte[] downloadExcel(DownloadExcelFiltersRequest request) {
        logger.info("Inicio del proceso de recolección de datos para descargar excel backup");
        List<Map<String, Object>> data = apifyRepository.getLastProcessedDataFromApifyConnection(request.getStartDate(), request.getFinishDate(), request.getTrackStartDate(), request.getAccountList());
        Set<String> campos = new HashSet<>();
        if (!data.isEmpty()) {
            campos.addAll(data.get(0).keySet());
        }

        List<String> notFound = request.getNotFoundUsername() == null ? List.of() : request.getNotFoundUsername();
        List<String> banned = request.getAccountUnavailable() == null ? List.of() : request.getAccountUnavailable();
        List<String> timeout = request.getTimeoutAccountList() == null ? List.of() : request.getTimeoutAccountList();

        for (String username : notFound) {
            Map<String, Object> registro = new HashMap<>();
            for (String campo : campos) {
                if (campo.equals("TikTok Username")) {
                    registro.put(campo, username);
                } else if (campo.equals("Views") || campo.equals("Likes") || campo.equals("Comments") ||
                        campo.equals("Reposted") || campo.equals("Saves") || campo.equals("Engagement rate") ||
                        campo.equals("Interactions") || campo.equals("Number of Hashtags")) {
                    registro.put(campo, 0); // campos numéricos
                } else {
                    registro.put(campo, "Not found: N/A"); // texto por defecto
                }
            }
            data.add(registro);
        }

        for (String username : banned) {
            Map<String, Object> registro = new HashMap<>();
            for (String campo : campos) {
                if (campo.equals("TikTok Username")) {
                    registro.put(campo, username); // cuenta real
                } else if (campo.equals("Views") || campo.equals("Likes") || campo.equals("Comments") ||
                        campo.equals("Reposted") || campo.equals("Saves") || campo.equals("Engagement rate") ||
                        campo.equals("Interactions") || campo.equals("Number of Hashtags")) {
                    registro.put(campo, 0); // campos numéricos
                } else {
                    registro.put(campo, "Account unavailable"); // texto por defecto para baneadas
                }
            }
            data.add(registro);
        }

        for (String timeoutAccount : timeout) {
            Map<String, Object> registro = new HashMap<>();
            for (String campo : campos) {
                if (campo.equals("TikTok Username")) {
                    registro.put(campo, timeoutAccount);
                } else if (campo.equals("Views") || campo.equals("Likes") || campo.equals("Comments") ||
                        campo.equals("Reposted") || campo.equals("Saves") || campo.equals("Engagement rate") ||
                        campo.equals("Interactions") || campo.equals("Number of Hashtags")) {
                    registro.put(campo, 0); // campos numéricos
                } else {
                    registro.put(campo, "Timeout Account"); // texto por defecto
                }
            }
            data.add(registro);
        }

        if (data.isEmpty()) {
            throw new ResourceNotFoundException("No hay datos para exportar a Excel");
        }

        logger.info("Inicio de la generación del archivo Excel");
        return excelService.downloadExcel(data);
    }

}
