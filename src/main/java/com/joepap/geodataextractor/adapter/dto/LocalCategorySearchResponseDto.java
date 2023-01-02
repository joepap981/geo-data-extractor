package com.joepap.geodataextractor.adapter.dto;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LocalCategorySearchResponseDto {
    private LocalMetaDto meta;
    private List<LocalDocumentDto> documents;
}
