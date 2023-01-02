package com.joepap.geodataextractor.adapter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LocalDocumentDto {
    @JsonProperty("place_name")
    private String placeName;
    @JsonProperty("distance")
    private String distance;
    @JsonProperty("place_url")
    private String placeUrl;
    @JsonProperty("category_name")
    private String categoryName;
    @JsonProperty("address_name")
    private String addressName;
    @JsonProperty("road_address_name")
    private String roadAddressName;
    @JsonProperty("id")
    private Long id;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("category_group_code")
    private CategoryGroupCode categoryGroupCode;
    @JsonProperty("category_group_name")
    private String categoryGroupName;
    @JsonProperty("x")
    private Double x;
    @JsonProperty("y")
    private Double y;
}
