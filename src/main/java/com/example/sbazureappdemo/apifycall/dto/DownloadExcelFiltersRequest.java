package com.example.sbazureappdemo.apifycall.dto;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DownloadExcelFiltersRequest {
    @JsonProperty("StartDate")
    private String startDate;
    @JsonProperty("FinishDate")
    private String finishDate;
    @JsonProperty("AccountList")
    private List<String> accountList;
    @JsonProperty("NotFoundAccountList")
    private List<String> NotFoundUsername;
    @JsonProperty("BannedAccountList")
    private List<String> accountUnavailable;
    @JsonProperty("TrackStartDate")
    private String trackStartDate;
    @JsonProperty("TimeoutAccountList")
    private List<String> timeoutAccountList;


    public String getTrackStartDate() {
        return trackStartDate;
    }

    public void setTrackStartDate(String trackStartDate) {
        this.trackStartDate = trackStartDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }



    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public List<String> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<String> accountList) {
        this.accountList = accountList;
    }




    public List<String> getTimeoutAccountList() {
        return timeoutAccountList;
    }

    public void setTimeoutAccountList(List<String> timeoutAccountList) {
        this.timeoutAccountList = timeoutAccountList;
    }



    public List<String> getNotFoundUsername() {
        return NotFoundUsername;
    }

    public void setNotFoundUsername(List<String> NotFoundUsername) {
        this.NotFoundUsername = NotFoundUsername;
    }


    public List<String> getAccountUnavailable() {
        return accountUnavailable;
    }

    public void setAccountUnavailable(List<String> accountUnavailable) {
        this.accountUnavailable = accountUnavailable;
    }

    
}
