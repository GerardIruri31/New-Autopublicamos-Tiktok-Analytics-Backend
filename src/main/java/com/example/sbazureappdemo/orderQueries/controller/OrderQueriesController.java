package com.example.sbazureappdemo.orderQueries.controller;


import com.example.sbazureappdemo.dbQueries.dto.FiltersRequestDTO;
import com.example.sbazureappdemo.ordenGeneration.controller.ordenGenerationFiltersController;
import com.example.sbazureappdemo.ordenGeneration.dto.PaResponseDTO;
import com.example.sbazureappdemo.ordenGeneration.service.OrdenGenerationService;
import com.example.sbazureappdemo.orderQueries.dto.CreatedByDTO;
import com.example.sbazureappdemo.orderQueries.dto.FiltersRequest;
import com.example.sbazureappdemo.orderQueries.dto.QueryResponse;
import com.example.sbazureappdemo.orderQueries.service.OrderQueriesService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order/queries")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrderQueriesController {

    Logger logger = LoggerFactory.getLogger(OrderQueriesController.class);
    private final OrderQueriesService orderQueriesService;

    @PreAuthorize("hasAnyRole('SUP','ADMIN','PA')")
    @PostMapping("/search")
    public ResponseEntity<List<QueryResponse>> search(@RequestBody FiltersRequest request) {
        logger.info("Iniciando search query Ordenes disponibles ... ");
        return ResponseEntity.ok(orderQueriesService.search(request));
    }

    @PreAuthorize("hasAnyRole('SUP','ADMIN','PA')")
    @GetMapping("/createdby")
    public ResponseEntity<List<CreatedByDTO>> createdBy() {
        logger.info("Iniciando obtención datos createdBy disponibles ... ");
        return ResponseEntity.ok(orderQueriesService.createdBy());
    }


    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody FiltersRequest request)  {
        logger.info("Inicio de la generación del archivo Excel - dbqueries");
        byte[] excelFile = orderQueriesService.downloadExcel(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "tiktok_metrics.xlsx");
        return ResponseEntity.ok().headers(headers).body(excelFile);
    }






}
