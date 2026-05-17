package com.example.sbazureappdemo.paGraphs.controller;
import com.example.sbazureappdemo.paGraphs.dto.PaParamsFiltersDTO;
import com.example.sbazureappdemo.paGraphs.service.PaGraphsService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/pagraphs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PaGraphsController {
    Logger logger = LoggerFactory.getLogger(PaGraphsController.class);
    private final PaGraphsService paGraphsService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/getdata")
    public ResponseEntity<List<Map<String,Object>>> RecibirFiltros(@RequestBody PaParamsFiltersDTO filtros) {
        logger.info("Iniciando obtención datos para PA's en rango fechas seleccionado ... ");
        return ResponseEntity.ok(paGraphsService.GetDatosPaGraphs(filtros));
    }
}
