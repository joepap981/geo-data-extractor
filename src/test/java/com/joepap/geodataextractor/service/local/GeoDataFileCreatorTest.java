package com.joepap.geodataextractor.service.local;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.apache.logging.log4j.util.Strings;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joepap.geodataextractor.adapter.KakaoLocalAdapter;
import com.joepap.geodataextractor.adapter.dto.CategoryGroupCode;
import com.joepap.geodataextractor.adapter.dto.LocalCategorySearchRequestDto;
import com.joepap.geodataextractor.adapter.dto.LocalCategorySearchResponseDto;
import com.joepap.geodataextractor.adapter.dto.LocalDocumentDto;
import com.joepap.geodataextractor.adapter.dto.LocalMetaDto;
import com.joepap.geodataextractor.adapter.vo.RectangleBuilderVo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoDataFileCreatorTest {

    @InjectMocks
    private GeoDataFileCreator target;
    @Mock
    private GeoDataRetrieveService geoDataRetrieveService;
    @Mock
    private KakaoLocalAdapter kakaoLocalAdapter;

    private static final CategoryGroupCode CATEGORY_GROUP_CODE = CategoryGroupCode.MT1;
    private static final String FILE_PATH = "./";
    private static final String API_KEY = "KEY";

    @Test
    void test() throws IOException {
        final LocalDocumentDto localDocumentDto = createDocument();
        when(localDocumentDto.getCategoryName()).thenReturn(Strings.EMPTY);
        when(kakaoLocalAdapter.searchLocalByCategory(anyString(), any()))
                .thenReturn(LocalCategorySearchResponseDto.builder()
                                    .documents(Lists.newArrayList(localDocumentDto))
                                    .meta(LocalMetaDto.builder()
                                                      .isEnd(true)
                                                      .pageableCount(15)
                                                      .totalCount(15)
                                                      .build()).build());
        target.createGeoCsvFileForCode(CATEGORY_GROUP_CODE, FILE_PATH);
        verify(geoDataRetrieveService, times(0)).retrieveRestOfLocalDocuments(any(), any());
        verify(kakaoLocalAdapter, times(1)).searchLocalByCategory(anyString(), any());
    }

    @Test
    void testCreateGeoCsvFileForCode_() throws IOException {
        final LocalDocumentDto localDocumentDto = createDocument();
        when(localDocumentDto.getCategoryName()).thenReturn(Strings.EMPTY);
        when(kakaoLocalAdapter.searchLocalByCategory(anyString(), any()))
                .thenReturn(LocalCategorySearchResponseDto.builder()
                                                          .documents(Lists.newArrayList(localDocumentDto))
                                                          .meta(LocalMetaDto.builder()
                                                                            .isEnd(false)
                                                                            .pageableCount(45)
                                                                            .totalCount(45)
                                                                            .build()).build());
        target.createGeoCsvFileForCode(CATEGORY_GROUP_CODE, FILE_PATH);
        verify(geoDataRetrieveService, times(1)).retrieveRestOfLocalDocuments(any(), any());
        verify(kakaoLocalAdapter, times(1)).searchLocalByCategory(anyString(), any());
    }
    private static LocalDocumentDto createDocument() {
        final LocalDocumentDto localDocumentDto = mock(LocalDocumentDto.class);
        when(localDocumentDto.getCategoryName()).thenReturn(Strings.EMPTY);
        when(localDocumentDto.getCategoryGroupCode()).thenReturn(CATEGORY_GROUP_CODE);
        return localDocumentDto;
    }
}