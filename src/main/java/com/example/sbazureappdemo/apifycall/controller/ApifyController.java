package com.example.sbazureappdemo.apifycall.controller;
import com.example.sbazureappdemo.apifycall.dto.ApifyRequestParamsDTO;
import com.example.sbazureappdemo.apifycall.service.apifyOrchestratorService;
import com.example.sbazureappdemo.excelService.ExcelColumnReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apifycall")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ApifyController {
    Logger logger = LoggerFactory.getLogger(ApifyController.class);
    private final apifyOrchestratorService apifyOrchestratorService;
    private final ExcelColumnReaderService excelColumnReaderService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/filtrar")
    public ResponseEntity<List<Map<String, Object>>> filtrarDatos(@RequestBody ApifyRequestParamsDTO request) {
        logger.info("Datos recibidos en el backend para Apify-Call:");
        logger.info("StartDate: " + request.getStartDate());
        logger.info("FinishDate: " + request.getFinishDate());
        logger.info("AccountList: " + request.getAccountList());
        logger.info("UserId: " + request.getUserId());
        logger.info("Iniciando llamado a API-APIFY ... ");
        return ResponseEntity.ok(apifyOrchestratorService.filtrarDatos(request.getStartDate(), request.getFinishDate(), request.getAccountList(),request.getUserId()));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/excel/read-tiktok-accounts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> readTiktokAccounts(@RequestParam("file") MultipartFile file) {
        String sheetName = "tiktok_metricas";
        String headerName = "tiktok_accounts";
        logger.info("Iniciando conversión de Excel a cuentas de tiktok ... ");
        List<String> accounts = excelColumnReaderService.leerColumna(file, sheetName, headerName);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("count", accounts.size());
        resp.put("values", accounts);
        return ResponseEntity.ok(resp);
    }
}

