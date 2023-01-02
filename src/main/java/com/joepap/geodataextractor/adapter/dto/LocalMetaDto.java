package com.joepap.geodataextractor.adapter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LocalMetaDto {
    @JsonProperty("same_name")
    private String sameName;
    @JsonProperty("pageable_count")
    private Integer pageableCount;
    @JsonProperty("total_count")
    private Integer totalCount;
    @JsonProperty("is_end")
    private Boolean isEnd;

    public boolean isAbleToSearchAllData() {
        return pageableCount >= totalCount;
    }
}