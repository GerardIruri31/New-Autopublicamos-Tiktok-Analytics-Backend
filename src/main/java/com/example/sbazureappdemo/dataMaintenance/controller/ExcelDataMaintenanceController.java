package com.example.sbazureappdemo.dataMaintenance.controller;
import com.example.sbazureappdemo.dataMaintenance.service.DataMaintenanceService;
import com.example.sbazureappdemo.dataMaintenance.service.ExcelUploadService;
import com.example.sbazureappdemo.excelService.ExcelService;

import com.example.sbazureappdemo.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/datamaintenance")
@RequiredArgsConstructor
public class ExcelDataMaintenanceController {
    Logger logger = LoggerFactory.getLogger(ExcelDataMaintenanceController.class);
    private final DataMaintenanceService dataMaintenanceService;
    private final ExcelService excelService;
    private final ExcelUploadService excelUploadService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam("TableName") String tableName) {
        List<Map<String, Object>> data = dataMaintenanceService.showDBRecords(tableName);
        if (data == null || data.isEmpty()) {
            logger.info("No hay datos para exportar.");
            throw new ResourceNotFoundException("No hay datos para exportar a Excel.");
        }
        logger.info("Inicio de la generación del archivo Excel en DataMaintenance");
        byte[] excelFile = excelService.downloadExcel(data);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "tiktok_metrics.xlsx");
        return ResponseEntity.ok().headers(headers).body(excelFile);
    }



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/uploadexcel")
    public ResponseEntity<Map<String, Object>> uploadExcelFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
        logger.info("Excel recibido correctamente en el backend en ImportExcel Datamaintenance");
        return ResponseEntity.ok().body(excelUploadService.processExcelFile(file,userId));

    }
}
