package com.joepap.geodataextractor.adapter;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.joepap.geodataextractor.adapter.dto.LocalCategorySearchRequestDto;
import com.joepap.geodataextractor.adapter.dto.LocalCategorySearchResponseDto;
import com.joepap.geodataextractor.adapter.util.MapperUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KakaoLocalAdapter extends AbstractAdapter {

    private static final String BASE_URI = "https://dapi.kakao.com";
    private static final String KEY_HEADER_PREFIX = "KakaoAK ";
    private static final String SEARCH_BY_CATEGORY_URI = "/v2/local/search/category.json";

    public LocalCategorySearchResponseDto searchLocalByCategory(
            String apiKey, LocalCategorySearchRequestDto requestDto) {
        final String uri = BASE_URI + SEARCH_BY_CATEGORY_URI;
        final MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        parameter.setAll(MapperUtil.toStringMap(requestDto));
        return get(uri, new ParameterizedTypeReference<>() {},
                   createBasicAuthHeaders(apiKey), parameter);
    }

    private static HttpHeaders createBasicAuthHeaders(final String apiKey){
        return new HttpHeaders() {{
            set(HttpHeaders.AUTHORIZATION, KEY_HEADER_PREFIX + apiKey);
        }};
    }
}
