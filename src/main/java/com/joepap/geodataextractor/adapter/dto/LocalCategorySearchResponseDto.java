package com.joepap.geodataextractor.adapter.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor@NoArgsConstructor
public class LocalCategorySearchResponseDto {
    private LocalMetaDto meta;
    private List<LocalDocumentDto> documents;
}
