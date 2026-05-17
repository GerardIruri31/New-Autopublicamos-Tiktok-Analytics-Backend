package com.example.sbazureappdemo.paGraphs.dto;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class PaParamsFiltersDTO {
    @JsonProperty("dateFrom")
    private String startDate;
    @JsonProperty("dateTo")
    private String finishDate;
    @JsonProperty("Publisher")
    private List<String> PAList;
}
