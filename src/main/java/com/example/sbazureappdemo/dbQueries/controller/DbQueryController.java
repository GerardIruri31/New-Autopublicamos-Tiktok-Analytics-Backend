package com.example.sbazureappdemo.dbQueries.controller;
import com.example.sbazureappdemo.dbQueries.service.DbQueryService;
import com.example.sbazureappdemo.dbQueries.dto.FiltersRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/databasequery")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DbQueryController {
    Logger logger = LoggerFactory.getLogger(DbQueryController.class);
    private final DbQueryService dbQueryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/filter")
    public ResponseEntity<List<Map<String, Object>>> filtrarDatos(@RequestBody FiltersRequestDTO request) {
        logger.info("Iniciando consulta DB-queries nativo ... ");
        return ResponseEntity.ok(dbQueryService.filtrarDatosPreview(request,20));
    }


    @PostMapping("/scorescene")
    public ResponseEntity<List<Map<String,Object>>> getScoreScene(@RequestBody FiltersRequestDTO request) {
        logger.info("Iniciando consulta SCORE SCENES ... ");
        return ResponseEntity.ok(dbQueryService.scoreScenesServicePreview(request,20));
    }


    @PostMapping("/conciso")
    public ResponseEntity<List<Map<String,Object>> > getReporteConciso(@RequestBody FiltersRequestDTO request) {
        logger.info("Iniciando consulta SHORT CONCISO ... ");
        return ResponseEntity.ok(dbQueryService.reporteConcisoServicePreview(request,20));
    }


    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam("reportType") String reportType, @RequestBody FiltersRequestDTO request)  {
        logger.info("Inicio de la generación del archivo Excel - dbqueries");
        byte[] excelFile = dbQueryService.switchDownloadButton(reportType, request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "tiktok_metrics.xlsx");
        return ResponseEntity.ok().headers(headers).body(excelFile);
    }


    @GetMapping("/posts")
    public ResponseEntity<Map<String,Object>> getCodautorasByCorreo() {
        List<Map<String,Object>> codautoras = dbQueryService.getTypePosts();
        return ResponseEntity.ok(Map.of("type_posts", codautoras));
    }
}