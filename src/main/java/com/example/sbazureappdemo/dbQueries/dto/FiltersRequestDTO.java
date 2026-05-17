package com.example.sbazureappdemo.dbQueries.dto;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FiltersRequestDTO {
    @JsonProperty("PubStartDate")
    private String PubStartDate;
    @JsonProperty("PubFinishtDate")
    private String PubFinishtDate;
    @JsonProperty("TrackStartDate")
    private String TrackStartDate;
    @JsonProperty("TrackFinishtDate")
    private String TrackFinishtDate;
    @JsonProperty("AuthorList")
    private List<String> AuthorList = List.of();
    @JsonProperty("BookList")
    private List<String> BookList = List.of();
    @JsonProperty("PAList")
    private List<String> PAList = List.of();
    @JsonProperty("SceneList")
    private List<String> SceneList = List.of();
    @JsonProperty("TypePostList")
    private List<String> TypePostList = List.of();
    @JsonProperty("AccountList")
    private List<String> AccountList = List.of();
    @JsonProperty("PostIDList")
    private List<String> PostIDList = List.of();
    @JsonProperty("RegionList")
    private List<String> RegionList = List.of();
    @JsonProperty("viewsMin")
    private String viewsMin;
    @JsonProperty("viewsMax")
    private String viewsMax;
    @JsonProperty("likesMin")
    private String likesMin;
    @JsonProperty("likesMax")
    private String likesMax;
    @JsonProperty("savesMin")
    private String savesMin;
    @JsonProperty("savesMax")
    private String savesMax;

    @JsonProperty("CommentsMin")
    private String CommentsMin;
    @JsonProperty("CommentsMax")
    private String CommentsMax;

    @JsonProperty("EngagementMin")
    private String EngagementMin;
    @JsonProperty("EngagementMax")
    private String EngagementMax;
    @JsonProperty("InteractionMin")
    private String InteractionMin;
    @JsonProperty("InteractionMax")
    private String InteractionMax;

}
