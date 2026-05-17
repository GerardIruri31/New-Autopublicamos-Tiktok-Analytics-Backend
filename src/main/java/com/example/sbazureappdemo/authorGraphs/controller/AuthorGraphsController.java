package com.example.sbazureappdemo.authorGraphs.controller;

import com.example.sbazureappdemo.authorGraphs.dto.AuthorGraphsDTO;
import com.example.sbazureappdemo.authorGraphs.dto.EfectividadAutorMetaDTO;
import com.example.sbazureappdemo.authorGraphs.dto.RegistroMesAutoraDTO;
import com.example.sbazureappdemo.authorGraphs.service.AuthorGraphsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/authorsgraphs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthorGraphsController {
    Logger logger = LoggerFactory.getLogger(AuthorGraphsController.class);
    private final AuthorGraphsService authorGraphsService;

    @PostMapping("/getdata")
    public CompletableFuture<ResponseEntity<List<List<Map<String,Object>>>>> RecibirFiltros(@RequestBody AuthorGraphsDTO filtros) {
        logger.info("Iniciando obtención datos para Authors en rango fechas seleccionado ... ");
        return authorGraphsService.GetDatosAuthorGraphs(filtros)
                .thenApply(ResponseEntity::ok);
    }


    @PostMapping("/dataPerMonth")
    public ResponseEntity<List<RegistroMesAutoraDTO>> dataPerMonth(@RequestBody AuthorGraphsDTO filtros) {
        logger.info("Iniciando obtención datos para Authors per month ... ");
        return ResponseEntity.ok(authorGraphsService.authorsPerMonth(filtros));

    }


    @PostMapping("/effectivenessAuthorPerMonth")
    public ResponseEntity<List<EfectividadAutorMetaDTO>> effectivenessPerMonth(@RequestBody AuthorGraphsDTO filtros) {
        logger.info("Iniciando obtención datos para Total posts Authors en rango fechas seleccionado ... ");
        return ResponseEntity.ok(authorGraphsService.effectAuthorsPerMonth(filtros));

    }
}