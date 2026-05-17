package com.example.sbazureappdemo.bookGraphs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BookGraphsRequestDTO {
    @JsonProperty("dateFrom")
    private String startDate;
    @JsonProperty("dateTo")
    private String finishDate;
    @JsonProperty("Author")
    private List<String> BookList;

}
