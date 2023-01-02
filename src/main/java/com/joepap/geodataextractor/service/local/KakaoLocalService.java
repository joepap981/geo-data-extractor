package com.joepap.geodataextractor.service.local;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.joepap.geodataextractor.adapter.KakaoLocalAdapter;
import com.joepap.geodataextractor.adapter.dto.CategoryGroupCode;
import com.joepap.geodataextractor.adapter.dto.LocalCategorySearchRequestDto;
import com.joepap.geodataextractor.adapter.dto.LocalCategorySearchResponseDto;
import com.joepap.geodataextractor.adapter.dto.LocalMetaDto;
import com.joepap.geodataextractor.adapter.vo.RectangleBuilderVo;
import com.joepap.geodataextractor.adapter.vo.RectangleBuilderVo.Point;
import com.joepap.geodataextractor.service.local.vo.OptimalZoneConditionVo;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLocalService {

    private final KakaoLocalAdapter kakaoLocalAdapter;

    private static final String API_KEY = "623024008f5c92b8c7bcea8339b38a24";
    private static final int MAX_PAGE_SIZE = 15;
    private static final int MAX_TOTAL_PAGE = 45;

    private static final double SEOUL_MIN_LONGITUDE = 126.7630;
    private static final double SEOUL_MAX_LATITUDE = 37.7020;
    private static final double SEOUL_MAX_LONGITUDE = 127.0520;
    private static final double SEOUL_MIN_LATITUDE = 37.4280;


    @PostConstruct
    public Map<CategoryGroupCode, OptimalZoneConditionVo> getOptimalZoneConditionMap() {

        final Map<CategoryGroupCode, OptimalZoneConditionVo> conditionVoMap =
                new EnumMap<>(CategoryGroupCode.class);

        for (CategoryGroupCode categoryGroupCode : CategoryGroupCode.values()) {
            conditionVoMap.put(categoryGroupCode, getOptimalZoneCondition(categoryGroupCode));
        }

        log.info("Map: {}", conditionVoMap);
        return conditionVoMap;
    }

    public OptimalZoneConditionVo getOptimalZoneCondition(CategoryGroupCode categoryGroupCode) {
        final double minimumDegree = 0.0001;
        double currentLonDegree = SEOUL_MAX_LONGITUDE - SEOUL_MIN_LONGITUDE;
        double currentLatDegree = SEOUL_MAX_LATITUDE - SEOUL_MIN_LATITUDE;
        int divideCount = 1;

        while (currentLonDegree > minimumDegree) {
            if (checkDataIncludedForZones(categoryGroupCode, currentLonDegree, currentLatDegree)) {
                break;
            }

            divideCount *= 2;
            currentLonDegree /= divideCount;
            currentLatDegree /= divideCount;
        }

        if (currentLonDegree < minimumDegree || currentLatDegree < minimumDegree) {
            throw new IllegalStateException();
        }
        return new OptimalZoneConditionVo(currentLonDegree, currentLatDegree);
    }
    
    public boolean checkDataIncludedForZones(
            CategoryGroupCode categoryGroupCode, double lonDegree, double latDegree) {
        int count = 0;
        double startLongitude = SEOUL_MIN_LONGITUDE;
        double endLongitude = startLongitude + lonDegree;
        while (true) {
            double startLatitude = SEOUL_MIN_LATITUDE;
            double endLatitude = startLatitude + latDegree;

            while (true) {
                final RectangleBuilderVo searchRectangle = RectangleBuilderVo.of(
                        new Point(startLongitude, startLatitude),
                        new Point(endLongitude, endLatitude)
                );

                log.info("{}", searchRectangle);
                if(!isAllDataIncluded(categoryGroupCode, searchRectangle)){
                    return false;
                }

                if (Double.compare(endLatitude, SEOUL_MAX_LATITUDE) == 0) {
                    break;
                }

                startLatitude = endLatitude;
                endLatitude += latDegree;
                if (endLatitude > SEOUL_MAX_LATITUDE) {
                    endLatitude = SEOUL_MAX_LATITUDE;
                }
                count++;
            }
            
            if (Double.compare(endLongitude, SEOUL_MAX_LONGITUDE) == 0) {
                break;
            }
            startLongitude = endLongitude;
            endLongitude += lonDegree;
            if (endLongitude > SEOUL_MAX_LONGITUDE) {
                endLongitude = SEOUL_MAX_LONGITUDE;
            }
        }

        log.info("count :{}", count);
        return true;
    } 

    private boolean isAllDataIncluded(
            CategoryGroupCode categoryGroupCode, RectangleBuilderVo rectangleBuilderVo) {
        final LocalCategorySearchRequestDto requestDto = LocalCategorySearchRequestDto.from(
                categoryGroupCode, rectangleBuilderVo, 45, MAX_PAGE_SIZE);
        final LocalCategorySearchResponseDto responseDto =
                kakaoLocalAdapter.searchLocalByCategory(API_KEY, requestDto);
        final LocalMetaDto meta = responseDto.getMeta();
        return meta.isAbleToSearchAllData();
    }
}