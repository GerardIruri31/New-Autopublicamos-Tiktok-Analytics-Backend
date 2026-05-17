package com.example.sbazureappdemo.bookGraphs.controller;

import com.example.sbazureappdemo.bookGraphs.dto.BookGraphsRequestDTO;
import com.example.sbazureappdemo.bookGraphs.dto.EfectividadBookMetaDTO;
import com.example.sbazureappdemo.bookGraphs.dto.RegistroMesLibroDTO;
import com.example.sbazureappdemo.bookGraphs.service.BookGraphsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/bookgraphs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BookGraphsController {
    Logger logger = LoggerFactory.getLogger(BookGraphsController.class);
    private final BookGraphsService bookGraphsService;

    @PostMapping("/dataPerMonth")
    public ResponseEntity<List<RegistroMesLibroDTO>> dataPerMonth(@RequestBody BookGraphsRequestDTO filtros) {
        logger.info("Iniciando obtención datos para Books por mes ... ");
        return ResponseEntity.ok(bookGraphsService.booksPerMonth(filtros));

    }

    @PostMapping("/effectivenessBookPerMonth")
    public ResponseEntity<List<EfectividadBookMetaDTO>> effectivenessPerMonth(@RequestBody BookGraphsRequestDTO filtros) {
        logger.info("Iniciando obtención datos para Total posts Books en rango fechas seleccionado ... ");
        return ResponseEntity.ok(bookGraphsService.effectBooksPerMonth(filtros));

    }
}