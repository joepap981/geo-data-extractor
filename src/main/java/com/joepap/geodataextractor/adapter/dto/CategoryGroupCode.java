package com.joepap.geodataextractor.adapter.dto;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.joepap.geodataextractor.service.local.AbstractGeoDataGenerateService;
import com.joepap.geodataextractor.service.local.CategoryBaseGeoDataFileGenerateService;
import com.joepap.geodataextractor.service.local.KeywordBaseGeoDataFileGenerateService;

import lombok.Getter;

@Getter
public enum CategoryGroupCode implements KakaoLocalType {
    MT1("대형마트", CategoryBaseGeoDataFileGenerateService.class),
    CS2("편의점", CategoryBaseGeoDataFileGenerateService.class),
    PS3("어린이집, 유치원", CategoryBaseGeoDataFileGenerateService.class),
    SC4("학교", CategoryBaseGeoDataFileGenerateService.class),
    AC5("학원", CategoryBaseGeoDataFileGenerateService.class),
    PK6("주차장", CategoryBaseGeoDataFileGenerateService.class),
    OL7("주유소, 충전소", CategoryBaseGeoDataFileGenerateService.class),
    SW8("지하철역", CategoryBaseGeoDataFileGenerateService.class),
    BK9("은행", CategoryBaseGeoDataFileGenerateService.class),
    CT1("문화시설", CategoryBaseGeoDataFileGenerateService.class),
    AG2("중개업소", CategoryBaseGeoDataFileGenerateService.class),
    PO3("공공기관", CategoryBaseGeoDataFileGenerateService.class),
    AT4("관광명소", CategoryBaseGeoDataFileGenerateService.class),
    AD5("숙박", CategoryBaseGeoDataFileGenerateService.class),
    FD6("음식점", CategoryBaseGeoDataFileGenerateService.class),
    CE7("카페", CategoryBaseGeoDataFileGenerateService.class),
    HP8("병원", CategoryBaseGeoDataFileGenerateService.class),
    PM9("약국", CategoryBaseGeoDataFileGenerateService.class),
    SO1("공유오피스", KeywordBaseGeoDataFileGenerateService.class),
    UNKNOWN_DEFAULT("UNKNOWN_DEFAULT", null)
    ;

    private final String categoryName;
    private final Class<? extends AbstractGeoDataGenerateService> classType;
    private static final Map<String, CategoryGroupCode> map =
            Arrays.stream(values())
                  .collect(Collectors.toMap(
                          CategoryGroupCode::name, Function.identity()));

    @Override
    public String getCode() {
        return name();
    }

    @JsonCreator
    public static CategoryGroupCode from(String code) {
        return map.getOrDefault(code, UNKNOWN_DEFAULT);
    }

    CategoryGroupCode(String categoryName, Class<? extends AbstractGeoDataGenerateService> classType) {
        this.categoryName = categoryName;
        this.classType = classType;
    }
}