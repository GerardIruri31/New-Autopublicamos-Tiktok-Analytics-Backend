package com.example.sbazureappdemo.apifycall.controller;
import com.example.sbazureappdemo.apifycall.dto.DownloadExcelFiltersRequest;

import com.example.sbazureappdemo.apifycall.service.ApifyDownloadExcelService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/apifycall/excel")
@RequiredArgsConstructor
public class ApifyExcelController {
    Logger logger = LoggerFactory.getLogger(ApifyExcelController.class);
    private final ApifyDownloadExcelService apifyDownloadExcelService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody DownloadExcelFiltersRequest request) {
        logger.info("AccountList: " + request.getAccountList());
        logger.info("NotFoundAccountList: " + request.getNotFoundUsername());
        logger.info("Account unavailable: " + request.getAccountUnavailable());
        byte[] excelFile = apifyDownloadExcelService.downloadExcel(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "tiktok_metrics.xlsx");
        return ResponseEntity.ok().headers(headers).body(excelFile);
    }
}
