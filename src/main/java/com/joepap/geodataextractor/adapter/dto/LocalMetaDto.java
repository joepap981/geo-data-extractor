package com.joepap.geodataextractor.adapter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor@NoArgsConstructor
public class LocalMetaDto {
    @JsonProperty("same_name")
    private String sameName;
    @JsonProperty("pageable_count")
    private Integer pageableCount;
    @JsonProperty("total_count")
    private Integer totalCount;
    @JsonProperty("is_end")
    private Boolean isEnd;

    public boolean isEnd() {
        return isEnd;
    }
    public boolean isAbleToSearchAllData() {
        return pageableCount >= totalCount;
    }
}