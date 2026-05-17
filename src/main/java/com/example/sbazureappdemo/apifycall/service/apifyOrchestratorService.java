package com.example.sbazureappdemo.apifycall.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class apifyOrchestratorService {
    private final apifyJsonProcessor apifyJsonProcessor;
    private final ApifyCallerService apifyCallerService;
    
    public List<Map<String, Object>> filtrarDatos(String startDate, String finishDate, List<String> accountList, String UserId) {
        // Llamar a la clase que se conecta a la API externa (python APIFY) con los filtros recibidos
        Map<String,Object> jsonResponse = apifyCallerService.fetchDataFromApi(startDate, finishDate, accountList);
        // Si hay error en la API, devolver mensaje de error en formato JSON
        if (jsonResponse.containsKey("error")) {
            return List.of(Map.of("error", jsonResponse));
        }
        return apifyJsonProcessor.procesarJson(jsonResponse, UserId);
    }



}
