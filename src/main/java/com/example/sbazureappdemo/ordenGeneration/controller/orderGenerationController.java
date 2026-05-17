package com.example.sbazureappdemo.ordenGeneration.controller;


import com.example.sbazureappdemo.ordenGeneration.dto.*;
import com.example.sbazureappdemo.ordenGeneration.service.OrdenGenerationService;
import com.example.sbazureappdemo.orderQueries.dto.FiltersRequest;
import com.example.sbazureappdemo.orderQueries.dto.QueryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class orderGenerationController {
    Logger logger = LoggerFactory.getLogger(orderGenerationController.class);
    private final OrdenGenerationService ordenGenerationService;

    @PreAuthorize("hasAnyRole('SUP','ADMIN')")
    @PostMapping("/auto")
    public ResponseEntity<AutoGenerationResultDTO> autoGeneration(@RequestBody FiltersRequestDTO request) {
        logger.info("Iniciando obtención generación órdenes ... ");
        return ResponseEntity.ok(ordenGenerationService.autoGeneration(request));
    }

    @PreAuthorize("hasAnyRole('SUP','ADMIN','PA')")
    @PutMapping("/edit/{codordentrabajo}")
    public ResponseEntity<OrderGenerationDetailResponseDTO> editOrder(@RequestBody NewManualOrderRequestDTO request, @PathVariable Long codordentrabajo) {
        logger.info("Iniciando EDIT de órden: " + codordentrabajo);
        return ResponseEntity.ok(ordenGenerationService.editOrder(request,codordentrabajo));
    }


    @PreAuthorize("hasAnyRole('SUP','ADMIN')")
    @PostMapping("/manual")
    public ResponseEntity<OrderGenerationDetailResponseDTO> manualOrder(@Valid @RequestBody NewManualOrderRequestDTO requestDTO) {
        logger.info("Iniciando insert datos Orden Manual ... ");
        return ResponseEntity.ok(ordenGenerationService.manualOrder(requestDTO));
    }




}
