package com.joepap.geodataextractor.service.local;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.joepap.geodataextractor.Constants;
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
public class OptimalZoneSizeCalculationService {

    private final KakaoLocalAdapter kakaoLocalAdapter;


    public Map<CategoryGroupCode, OptimalZoneConditionVo> getOptimalZoneConditionMap() {

        final Map<CategoryGroupCode, OptimalZoneConditionVo> conditionVoMap =
                new EnumMap<>(CategoryGroupCode.class);

//        for (CategoryGroupCode categoryGroupCode : CategoryGroupCode.values()) {
//            conditionVoMap.put(categoryGroupCode, getOptimalZoneCondition(categoryGroupCode));
//        }
        conditionVoMap.put(CategoryGroupCode.MT1, getOptimalZoneCondition(CategoryGroupCode.MT1));

        log.info("Map: {}", conditionVoMap);
        return conditionVoMap;
    }

    private OptimalZoneConditionVo getOptimalZoneCondition(CategoryGroupCode categoryGroupCode) {
        final double minimumDegree = 0.0001;
        double currentLonDegree = Constants.SEOUL_MAX_LONGITUDE - Constants.SEOUL_MIN_LONGITUDE;
        double currentLatDegree = Constants.SEOUL_MAX_LATITUDE - Constants.SEOUL_MIN_LATITUDE;
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
    
    private boolean checkDataIncludedForZones(
            CategoryGroupCode categoryGroupCode, double lonDegree, double latDegree) {
        int count = 0;
        double startLongitude = Constants.SEOUL_MIN_LONGITUDE;
        double endLongitude = startLongitude + lonDegree;
        while (true) {
            double startLatitude = Constants.SEOUL_MIN_LATITUDE;
            double endLatitude = startLatitude + latDegree;

            while (true) {
                final RectangleBuilderVo searchRectangle = RectangleBuilderVo.of(
                        new Point(startLongitude, endLatitude),
                        new Point(endLongitude, startLatitude)
                );

                log.info("{}", searchRectangle);
                if(!isAllDataIncluded(categoryGroupCode, searchRectangle)){
                    return false;
                }

                if (Double.compare(endLatitude, Constants.SEOUL_MAX_LATITUDE) == 0) {
                    break;
                }

                startLatitude = endLatitude;
                endLatitude += latDegree;
                if (endLatitude > Constants.SEOUL_MAX_LATITUDE) {
                    endLatitude = Constants.SEOUL_MAX_LATITUDE;
                }
                count++;
            }
            
            if (Double.compare(endLongitude, Constants.SEOUL_MAX_LONGITUDE) == 0) {
                break;
            }
            startLongitude = endLongitude;
            endLongitude += lonDegree;
            if (endLongitude > Constants.SEOUL_MAX_LONGITUDE) {
                endLongitude = Constants.SEOUL_MAX_LONGITUDE;
            }
        }

        log.info("count :{}", count);
        return true;
    } 

    private boolean isAllDataIncluded(
            CategoryGroupCode categoryGroupCode, RectangleBuilderVo rectangleBuilderVo) {
        final LocalCategorySearchRequestDto requestDto = LocalCategorySearchRequestDto.from(
                categoryGroupCode, rectangleBuilderVo, Constants.MAX_TOTAL_PAGE, Constants.MAX_PAGE_SIZE);
        final LocalCategorySearchResponseDto responseDto =
                kakaoLocalAdapter.searchLocalByCategory(Constants.API_KEY, requestDto);
        final LocalMetaDto meta = responseDto.getMeta();
        return meta.isAbleToSearchAllData();
    }
}