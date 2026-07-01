package com.example.sbazureappdemo.dataMaintenance.controller;
import com.example.sbazureappdemo.dataMaintenance.service.DataMaintenanceService;
import com.example.sbazureappdemo.dataMaintenance.service.ExcelUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/datamaintenance")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DataMaintenanceController {
    Logger logger = LoggerFactory.getLogger(DataMaintenanceController.class);
    private final DataMaintenanceService dataMaintenanceService;

    @PreAuthorize("hasAnyRole('ADMIN','SUP','PA')")
    @PostMapping("/tablerecords")
    public List<Map<String, Object>> RenderizeDBRecords(@RequestBody Map<String,Object> TableName) {
        logger.info("Iniciando obtención datos para showRecords: tabla " + TableName);
        return dataMaintenanceService.showDBRecordsPreview(TableName.get("TableName"));
    }
}
