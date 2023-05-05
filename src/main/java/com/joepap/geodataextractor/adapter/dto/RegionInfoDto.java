package com.joepap.geodataextractor.adapter.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor@NoArgsConstructor
public class RegionInfoDto {
    @JsonProperty("region")
    private List<String> region;
    @JsonProperty("keyword")
    private String keyword;
    @JsonProperty("selected_region")
    private String selectedRegion;
}
