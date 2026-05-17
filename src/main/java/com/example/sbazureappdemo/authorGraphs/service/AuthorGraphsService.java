package com.example.sbazureappdemo.authorGraphs.service;

import com.example.sbazureappdemo.authorGraphs.dto.AuthorGraphsDTO;
import com.example.sbazureappdemo.authorGraphs.dto.EfectividadAutorMetaDTO;
import com.example.sbazureappdemo.authorGraphs.dto.RegistroMesAutoraDTO;
import com.example.sbazureappdemo.authorGraphs.repository.AuthorGraphsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthorGraphsService {
    Logger logger = LoggerFactory.getLogger(AuthorGraphsService.class);
    private final AuthorGraphsRepository authorGraphsRepository;


    public CompletableFuture<List<List<Map<String, Object>>>> GetDatosAuthorGraphs(AuthorGraphsDTO filtros) {
        logger.info("Inicio del proceso de recolección de datos de autoras a la BD");
        List<String> autoras = filtros.getAuthorList();
        if (autoras == null || autoras.isEmpty()) {
            throw new IllegalArgumentException("La lista de autoras no puede estar vacía.");
        }
        CompletableFuture<List<Map<String, Object>>> futureQuery1 = CompletableFuture.supplyAsync(() ->
                authorGraphsRepository.GetDatosAuthorGraphsConnectionDBQuery1(filtros));
        CompletableFuture<List<Map<String, Object>>> futureQuery2 = CompletableFuture.supplyAsync(() ->
                authorGraphsRepository.GetDatosAuthorGraphsConnectionDBQuery2(filtros));

        logger.info("Datos obtenidos de las 2 queries completadas con éxito");
        return CompletableFuture.allOf(futureQuery1, futureQuery2)
                .thenApply(ignored -> {
                    List<List<Map<String, Object>>> resultadoFinal = Arrays.asList(futureQuery1.join(), futureQuery2.join());
                    logger.info("========== AUTHOR GRAPHS - RESULTADO FINAL ==========");
                    logger.info("Resultado Final: {}", resultadoFinal);
                    return resultadoFinal;
                });
    }


    public List<RegistroMesAutoraDTO> authorsPerMonth(AuthorGraphsDTO filtros) {
        logger.info("Inicio del proceso data authors per month");
        List<String> autoras = filtros.getAuthorList();
        if (autoras == null || autoras.isEmpty()) {
            throw new IllegalArgumentException("La lista de autoras no puede estar vacía.");
        }
        return authorGraphsRepository.dataAuthorsPerMonth(filtros);
    }


    public List<EfectividadAutorMetaDTO> effectAuthorsPerMonth(AuthorGraphsDTO filtros) {
        List<String> autoras = filtros.getAuthorList();
        if (autoras == null || autoras.isEmpty()) {
            throw new IllegalArgumentException("La lista de autoras no puede estar vacía.");
        }
        logger.info("Inicio del proceso efectividad authors per month");
        return authorGraphsRepository.effectDataAuthorsPerMonth(filtros);
    }

}
