package com.joepap.geodataextractor.adapter.dto;

import com.joepap.geodataextractor.service.local.vo.LocalSearchRequestVo;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class LocalKeywordSearchRequestDto {

    @NotNull
    private String query;
//    @JsonSerialize(using = KakaoLocalTypeSerializer.class)
//    @JsonProperty("category_group_code")
//    private CategoryGroupCode categoryGroupCode;
    private String rect;
    private Integer page;
    private Integer size;

    public static LocalKeywordSearchRequestDto from(LocalSearchRequestVo requestVo) {
        return builder()
                .query(requestVo.getCategoryGroupCode().getCategoryName())
                .rect(requestVo.getRect().getRectString())
                .page(requestVo.getPage())
                .size(requestVo.getSize())
                .build();
    }
}
