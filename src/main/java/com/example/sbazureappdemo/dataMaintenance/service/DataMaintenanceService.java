package com.example.sbazureappdemo.dataMaintenance.service;
import java.util.*;

import com.example.sbazureappdemo.dataMaintenance.repository.DataMaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DataMaintenanceService {
    Logger logger = LoggerFactory.getLogger(DataMaintenanceService.class);
    private final DataMaintenanceRepository dataMaintenanceRepository;

    public List<Map<String,Object>> showDBRecordsPreview(Object tableName) {
        String tabla = String.valueOf(tableName);
        if (!tabla.matches("[A-Za-z0-9_\\.]+")) {
            throw new IllegalArgumentException("Invalid table name");
        }
        logger.info("Inicio del proceso mostrar preview contenido tb: " + tabla);
        return dataMaintenanceRepository.showDBRecordsConnectionPreview(tableName, 20);
    }

    public List<Map<String,Object>> showDBRecords(Object tableName) {
        String tabla = String.valueOf(tableName);
        if (!tabla.matches("[A-Za-z0-9_\\.]+")) {
            throw new IllegalArgumentException("Invalid table name");
        }
        logger.info("Iniciando obtención de datos para la base de datos.");
        return dataMaintenanceRepository.showDBRecordsConnection(tableName);
    }



}