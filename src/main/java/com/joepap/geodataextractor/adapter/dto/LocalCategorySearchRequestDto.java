package com.joepap.geodataextractor.adapter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joepap.geodataextractor.adapter.serializer.KakaoLocalTypeSerializer;
import com.joepap.geodataextractor.adapter.vo.RectangleBuilderVo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class LocalCategorySearchRequestDto {
    @JsonSerialize(using = KakaoLocalTypeSerializer.class)
    @JsonProperty("category_group_code")
    private CategoryGroupCode categoryGroupCode;
    private String rect;
    private Integer page;
    private Integer size;

    public static LocalCategorySearchRequestDto from(
            CategoryGroupCode categoryGroupCode, RectangleBuilderVo rectangleBuilderVo, int page, int size) {
        return builder()
                .categoryGroupCode(categoryGroupCode)
                .rect(rectangleBuilderVo.getRectString())
                .page(page)
                .size(size)
                .build();
    }
}
