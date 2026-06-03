package com.example.sbazureappdemo.ordenGeneration.dto;

import lombok.Data;

@Data
public class DeleteOrderResponseDTO {
    private Integer ctdordenesupdated;
    private Integer ctdimagenesupdate;
    private Integer ctdcuentasupdate;
    private Integer ctdsonidosdelete;
    private Integer o_ctdlibrosdelete;
    private String coderror;
    private String deserror;
}
