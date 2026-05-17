package com.example.sbazureappdemo.ordenGeneration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoGenerationResponseDTO {
    private Long codcabeceraordentrabajo;
    private Integer ctdordenes;
    private Integer ctdordenescompleta;
    private Integer ctdordenesincompleta;
    private String msj_error_log;

}
