package com.joepap.geodataextractor.service.local;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
    private static final double MIN_DEGREE_DIFFERENCE = 0.00003;

    private Set<Long> idSet;
    private List<LocalDocumentDto> documents;
    private CSVPrinter csvPrinter;
    private CategoryGroupCode categoryGroupCode;

    private void init(CategoryGroupCode categoryGroupCode, String fileName) throws IOException {
        this.categoryGroupCode = categoryGroupCode;
        documents = Lists.newArrayList();
        idSet = Sets.newHashSet();
        final FileWriter fileWriter = new FileWriter(fileName);
        csvPrinter = new CSVPrinter(fileWriter, GeoDataVo.getCsvFormat());
    }

    public String createGeoCsvFileForCode(CategoryGroupCode categoryGroupCode, String filePath)
            throws IOException {
        filePath = filePath == null ? Strings.EMPTY : filePath;
        final String fileName = filePath + '/'
                                + categoryGroupCode
                                + '_'+ categoryGroupCode.getCategoryName()
                                + '_' + LocalDate.now() + Constants.CSV_EXTENSION;
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

        if (searchZone.isLessThanMinDegree(MIN_DEGREE_DIFFERENCE)) {
            addDocumentsToList(responseDto, searchZone);
            createMockDataForUnsearchableDocuments(responseDto, searchZone);
        } else if (responseDto.getMeta().isAbleToSearchAllData()) {
            addDocumentsToList(responseDto, searchZone);
        } else {
            final List<RectangleBuilderVo> newZones = Lists.newArrayList(
                    searchZone.getLeftTopZone(), searchZone.getRightTopZone(),
                    searchZone.getLeftBottomZone(), searchZone.getRightBottomZone());
            for (RectangleBuilderVo newZone : newZones) {
                writeCategoryData(newZone);
            }
        }

        if (documents.size() > FLUSH_THRESHOLD) {
            flushGeoDataToCsv();
        }
    }

    private void addDocumentsToList(LocalCategorySearchResponseDto responseDto, RectangleBuilderVo searchZone) {
        System.out.println("[" + categoryGroupCode + "] Saving data from zone : "
                           + searchZone.getRectString());
        addAll(responseDto.getDocuments());
        if (!responseDto.getMeta().isEnd()) {
            final List<LocalDocumentDto> restOfDocuments = geoDataRetrieveService.retrieveRestOfLocalDocuments(
                    categoryGroupCode, searchZone);
            addAll(restOfDocuments);
        }
    }

    private void createMockDataForUnsearchableDocuments(
            LocalCategorySearchResponseDto responseDto, RectangleBuilderVo searchZone) {
        final int unsearchableCount = responseDto.getMeta().getUnsearchableDocumentCount();
        final LocalDocumentDto sampleDocument = responseDto.getDocuments().get(0);
        for (int i = 0; i < unsearchableCount; i++) {
            final LocalDocumentDto mockDocument =
                    LocalDocumentDto.builder()
                                    .id(Constants.MOCK_DATA_ID)
                                    .x(sampleDocument.getX())
                                    .y(sampleDocument.getY())
                                    .addressName(sampleDocument.getAddressName())
                                    .categoryGroupCode(sampleDocument.getCategoryGroupCode())
                                    .categoryGroupName(sampleDocument.getCategoryGroupName())
                                    .roadAddressName(sampleDocument.getRoadAddressName())
                                    .build();
            documents.add(mockDocument);
        }
        final String message = String.format("[%s] Saved %s mock data from zone : %s",
                                             categoryGroupCode, unsearchableCount, searchZone.getRectString());
        System.out.println(message);
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
                                    geoDataVo.getSubCategory4(),
                                    geoDataVo.isMockData());
                           } catch (IOException e) {
                               throw new RuntimeException(e);
                           }
        });
        documents.clear();
    }

    private void addAll(List<LocalDocumentDto> localDocuments) {
        for (LocalDocumentDto localDocumentDto : localDocuments) {
            if (idSet.contains(localDocumentDto.getId())) {
                continue;
            }
            documents.add(localDocumentDto);
            idSet.add(localDocumentDto.getId());
        }
    }

    @PreDestroy
    public void close() throws IOException {
        idSet.clear();
        documents.clear();
        csvPrinter.close();
    }
}