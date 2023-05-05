package com.joepap.geodataextractor.service.local.vo;

import com.joepap.geodataextractor.adapter.dto.CategoryGroupCode;
import com.joepap.geodataextractor.adapter.vo.RectangleBuilderVo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class LocalSearchRequestVo {
    private CategoryGroupCode categoryGroupCode;
    private RectangleBuilderVo rect;
    private int page;
    private int size;

    public static LocalSearchRequestVo from(
            CategoryGroupCode categoryGroupCode, RectangleBuilderVo rectangleBuilderVo, int page, int size) {
        return builder()
                .categoryGroupCode(categoryGroupCode)
                .rect(rectangleBuilderVo)
                .page(page)
                .size(size)
                .build();
    }
}
