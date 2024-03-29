package com.joepap.geodataextractor.adapter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joepap.geodataextractor.adapter.serializer.KakaoLocalTypeSerializer;
import com.joepap.geodataextractor.service.local.vo.LocalSearchRequestVo;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class LocalCategorySearchRequestDto {
    @NotNull
    @JsonSerialize(using = KakaoLocalTypeSerializer.class)
    @JsonProperty("category_group_code")
    private CategoryGroupCode categoryGroupCode;
    private String rect;
    private Integer page;
    private Integer size;

    public static LocalCategorySearchRequestDto from(LocalSearchRequestVo requestVo) {
        return builder()
                .categoryGroupCode(requestVo.getCategoryGroupCode())
                .rect(requestVo.getRect().getRectString())
                .page(requestVo.getPage())
                .size(requestVo.getSize())
                .build();
    }
}
