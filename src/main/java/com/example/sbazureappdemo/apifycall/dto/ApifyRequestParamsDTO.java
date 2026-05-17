package com.example.sbazureappdemo.apifycall.dto;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApifyRequestParamsDTO {
    @JsonProperty("StartDate")
    private String startDate;
    @JsonProperty("FinishDate")
    private String finishDate;
    @JsonProperty("AccountList")
    private List<String> accountList;
    @JsonProperty("UserId")
    private String UserId;
}  
