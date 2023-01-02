package com.joepap.geodataextractor.service.local;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.joepap.geodataextractor.Constants;
import com.joepap.geodataextractor.adapter.dto.CategoryGroupCode;
import com.joepap.geodataextractor.adapter.dto.LocalDocumentDto;
import com.joepap.geodataextractor.adapter.vo.RectangleBuilderVo;
import com.joepap.geodataextractor.adapter.vo.RectangleBuilderVo.Point;
import com.joepap.geodataextractor.service.local.vo.GeoDataVo;
import com.joepap.geodataextractor.service.local.vo.OptimalZoneConditionVo;
import com.joepap.geodataextractor.util.ListTransformer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateGeoDataService {

    private final OptimalZoneSizeCalculationService optimalZoneSizeCalculationService;
    private final GeoDataRetrieveService geoDataRetrieveService;

    private static final int FLUSH_THRESHOLD = 100;
    private static final String SEOUL = "서울";

    @PostConstruct
    public void createGeoCsvFile() throws IOException {
        final Map<CategoryGroupCode, OptimalZoneConditionVo> conditionVoMap =
                optimalZoneSizeCalculationService.getOptimalZoneConditionMap();

        // Open CSV
        try (final FileWriter fileWriter = new FileWriter("test.csv")) {
            final CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);
            for (Map.Entry<CategoryGroupCode, OptimalZoneConditionVo> entry : conditionVoMap.entrySet()) {
                writeCategoryData(csvPrinter, entry.getKey(), entry.getValue());
            }
        }
    }

    public void writeCategoryData(
            CSVPrinter csvPrinter, CategoryGroupCode categoryGroupCode, OptimalZoneConditionVo conditionVo)
            throws IOException {
        final List<LocalDocumentDto> localDocuments = Lists.newArrayList();
        final double lonDegree = conditionVo.getLongitudeDegrees();
        final double latDegree = conditionVo.getLatitudeDegrees();
        // 불러오다가
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
                localDocuments.addAll(
                        geoDataRetrieveService.retrieveAllLocalDocuments(categoryGroupCode, searchRectangle));

                if (localDocuments.size() > FLUSH_THRESHOLD) {
                    flushGeoDataToCsv(csvPrinter, localDocuments);
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
        flushGeoDataToCsv(csvPrinter, localDocuments);
        log.info("count :{}", count);
    }

    public static void flushGeoDataToCsv(CSVPrinter printer, List<LocalDocumentDto> documents)
            throws IOException {
        ListTransformer.transform(documents, GeoDataVo::from).stream()
                       .filter(geoDataVo -> geoDataVo.getCity().equals(SEOUL))
                       .forEach(geoDataVo -> {
                           try {
                                printer.printRecord(
                                geoDataVo.getId(),
                                geoDataVo.getCategoryGroupCode(),
                                geoDataVo.getCategoryGroupName(),
                                geoDataVo.getPlaceName(),
                                geoDataVo.getAddress(),
                                geoDataVo.getRoadAddress(),
                                geoDataVo.getAdministrativeRegionName(),
                                geoDataVo.getAdministrativeRegionCode(),
                                geoDataVo.getLongitude(),
                                geoDataVo.getLatitude(),
                                geoDataVo.getSubCategory1(),
                                geoDataVo.getSubCategory2(),
                                geoDataVo.getSubCategory3(),
                                geoDataVo.getSubCategory4());
                           } catch (IOException e) {
                               throw new RuntimeException(e);
                           }
        });
        documents.clear();
    }
}
