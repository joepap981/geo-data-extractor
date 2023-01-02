package com.joepap.geodataextractor.service.local;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.joepap.geodataextractor.Constants;
import com.joepap.geodataextractor.adapter.KakaoLocalAdapter;
import com.joepap.geodataextractor.adapter.dto.CategoryGroupCode;
import com.joepap.geodataextractor.adapter.dto.LocalCategorySearchRequestDto;
import com.joepap.geodataextractor.adapter.dto.LocalCategorySearchResponseDto;
import com.joepap.geodataextractor.adapter.dto.LocalDocumentDto;
import com.joepap.geodataextractor.adapter.vo.RectangleBuilderVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeoDataRetrieveService {

    private final KakaoLocalAdapter kakaoLocalAdapter;

    public List<LocalDocumentDto> retrieveAllLocalDocuments(
            CategoryGroupCode categoryGroupCode, RectangleBuilderVo rectangleBuilderVo) {

        final List<LocalDocumentDto> localDocuments = Lists.newArrayList();
        for (int i = 1; i <= Constants.MAX_TOTAL_PAGE; i++) {
            final LocalCategorySearchResponseDto responseDto = kakaoLocalAdapter.searchLocalByCategory(
                    Constants.API_KEY, LocalCategorySearchRequestDto.from(
                            categoryGroupCode, rectangleBuilderVo, i, Constants.MAX_PAGE_SIZE));

            localDocuments.addAll(responseDto.getDocuments());
            if (responseDto.getMeta().getIsEnd()) {
                break;
            }
        }
        return localDocuments;
    }
}
