package com.example.sbazureappdemo.paGraphs.service;

import com.example.sbazureappdemo.paGraphs.dto.PaParamsFiltersDTO;
import com.example.sbazureappdemo.paGraphs.repository.PaGraphsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaGraphsService {
    Logger logger = LoggerFactory.getLogger(PaGraphsService.class);
    private final PaGraphsRepository paGraphsRepository;

    public List<Map<String,Object>> GetDatosPaGraphs(PaParamsFiltersDTO filtros) {
        List<String> pas = filtros.getPAList();
        if (pas == null || pas.isEmpty()) {
            throw new IllegalArgumentException("La lista de PA's no puede estar vacía");
        }
        logger.info("Inicio del proceso de recolección de datos de PA's a la BD");
        return paGraphsRepository.GetDatosPaGraphConnectionDB(filtros);
    }
}
