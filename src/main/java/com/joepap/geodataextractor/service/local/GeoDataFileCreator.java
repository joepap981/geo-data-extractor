package com.joepap.geodataextractor.service.local;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.joepap.geodataextractor.Constants;
import com.joepap.geodataextractor.adapter.KakaoLocalAdapter;
import com.joepap.geodataextractor.adapter.dto.CategoryGroupCode;
import com.joepap.geodataextractor.adapter.dto.LocalCategorySearchRequestDto;
import com.joepap.geodataextractor.adapter.dto.LocalCategorySearchResponseDto;
import com.joepap.geodataextractor.adapter.dto.LocalDocumentDto;
import com.joepap.geodataextractor.adapter.vo.RectangleBuilderVo;
import com.joepap.geodataextractor.service.local.vo.GeoDataVo;
import com.joepap.geodataextractor.util.ListTransformer;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class GeoDataFileCreator {
    private final GeoDataRetrieveService geoDataRetrieveService;
    private final KakaoLocalAdapter kakaoLocalAdapter;

    private static final int FLUSH_THRESHOLD = 100;
    private static final String SEOUL = "서울";

    private List<LocalDocumentDto> documents;
    private CSVPrinter csvPrinter;
    private CategoryGroupCode categoryGroupCode;

    private void init(CategoryGroupCode categoryGroupCode, String fileName) throws IOException {
        this.categoryGroupCode = categoryGroupCode;
        documents = Lists.newArrayList();
        final FileWriter fileWriter = new FileWriter(fileName);
        csvPrinter = new CSVPrinter(fileWriter, GeoDataVo.getCsvFormat());
    }

    public String createGeoCsvFileForCode(CategoryGroupCode categoryGroupCode, String filePath)
            throws IOException {
        filePath = filePath == null ? Strings.EMPTY : filePath;
        final String fileName = filePath + '/' + categoryGroupCode + '_' + LocalDate.now() + Constants.CSV_EXTENSION;
        System.out.println("Creating file : " + fileName);
        init(categoryGroupCode, fileName);
        writeCategoryData(RectangleBuilderVo.seoul());
        flushGeoDataToCsv();
        System.out.println("Created file : " + fileName);
        close();
        return fileName;
    }

    private void writeCategoryData(RectangleBuilderVo searchZone) throws IOException {
        final LocalCategorySearchRequestDto requestDto = LocalCategorySearchRequestDto.from(
                categoryGroupCode, searchZone, 1, Constants.MAX_PAGE_SIZE);
        final LocalCategorySearchResponseDto responseDto =
                kakaoLocalAdapter.searchLocalByCategory(KeyStorage.get(), requestDto);

        if (responseDto.getMeta().getTotalCount() == 0) {
            System.out.println("[" + categoryGroupCode + "] No data found in zone : "
                               + searchZone.getRectString());
            return;
        }

        if (responseDto.getMeta().isAbleToSearchAllData()) {
            System.out.println("[" + categoryGroupCode + "] Saving data from zone : "
                               + searchZone.getRectString());
            addDocumentsToList(responseDto, searchZone);
        } else {
            writeCategoryData(searchZone.getLeftTop());
            writeCategoryData(searchZone.getRightTop());
            writeCategoryData(searchZone.getLeftBottom());
            writeCategoryData(searchZone.getRightBottom());
        }

        if (documents.size() > FLUSH_THRESHOLD) {
            flushGeoDataToCsv();
        }
    }

    private void addDocumentsToList(LocalCategorySearchResponseDto responseDto, RectangleBuilderVo searchZone) {
        documents.addAll(responseDto.getDocuments());
        if (!responseDto.getMeta().isEnd()) {
            documents.addAll(geoDataRetrieveService.retrieveRestOfLocalDocuments(
                    categoryGroupCode, searchZone));
        }
    }

    private void flushGeoDataToCsv() throws IOException {
        ListTransformer.transform(documents, GeoDataVo::from).stream()
                       .filter(geoDataVo -> geoDataVo.getCity().equals(SEOUL))
                       .forEach(geoDataVo -> {
                           try {
                                csvPrinter.printRecord(
                                    geoDataVo.getId(),
                                    geoDataVo.getCategoryGroupCode(),
                                    geoDataVo.getCategoryGroupName(),
                                    geoDataVo.getPlaceName(),
                                    geoDataVo.getCity(),
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

    @PreDestroy
    public void close() throws IOException {
        documents.clear();
        csvPrinter.close();
    }
}
