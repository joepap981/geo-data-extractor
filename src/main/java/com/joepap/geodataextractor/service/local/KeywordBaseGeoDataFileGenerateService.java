package com.joepap.geodataextractor.service.local;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joepap.geodataextractor.adapter.KakaoLocalAdapter;
import com.joepap.geodataextractor.adapter.dto.LocalCategorySearchResponseDto;
import com.joepap.geodataextractor.adapter.dto.LocalKeywordSearchRequestDto;
import com.joepap.geodataextractor.service.local.vo.LocalSearchRequestVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class KeywordBaseGeoDataFileGenerateService extends AbstractGeoDataGenerateService {
    private final KakaoLocalAdapter kakaoLocalAdapter;

    @Override
    LocalCategorySearchResponseDto searchLocal(String key, LocalSearchRequestVo requestVo) {
        final LocalKeywordSearchRequestDto requestDto = LocalKeywordSearchRequestDto.from(requestVo);
        return kakaoLocalAdapter.searchLocalByKeyword(key, requestDto);
    }
}