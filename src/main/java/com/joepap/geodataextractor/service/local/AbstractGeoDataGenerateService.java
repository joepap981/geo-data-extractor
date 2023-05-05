package com.joepap.geodataextractor.service.local;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.util.Strings;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.joepap.geodataextractor.Constants;
import com.joepap.geodataextractor.adapter.dto.CategoryGroupCode;
import com.joepap.geodataextractor.adapter.dto.LocalCategorySearchResponseDto;
import com.joepap.geodataextractor.adapter.dto.LocalDocumentDto;
import com.joepap.geodataextractor.adapter.vo.RectangleBuilderVo;
import com.joepap.geodataextractor.service.local.type.ExtractAreaType;
import com.joepap.geodataextractor.service.local.vo.GeoDataVo;
import com.joepap.geodataextractor.service.local.vo.LocalSearchRequestVo;
import com.joepap.geodataextractor.util.ListTransformer;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractGeoDataGenerateService {

    private static final int FLUSH_THRESHOLD = 100;
    private static final double MIN_DEGREE_DIFFERENCE = 0.00003;

    private Set<Long> idSet;
    private List<LocalDocumentDto> documents;
    private CSVPrinter csvPrinter;
    private CategoryGroupCode categoryGroupCode;

    abstract LocalCategorySearchResponseDto searchLocal(String key, LocalSearchRequestVo requestVo);

    private void init(CategoryGroupCode categoryGroupCode, String fileName) throws IOException {
        this.categoryGroupCode = categoryGroupCode;
        documents = Lists.newArrayList();
        idSet = Sets.newHashSet();
        final FileWriter fileWriter = new FileWriter(fileName);
        csvPrinter = new CSVPrinter(fileWriter, GeoDataVo.getCsvFormat());
    }

    public String createGeoCsvFileForCode(
            ExtractAreaType extractAreaType, CategoryGroupCode categoryGroupCode, String filePath)
            throws IOException {
        if (!KeyStorage.hasActiveKey()) {
            throw new RuntimeException(
                    "No active key is registered. Please register using 'register-key' "
                    + "(use help register-key for details)");
        }
        filePath = filePath == null ? Strings.EMPTY : filePath;
        final String fileName = filePath + '/'
                                + categoryGroupCode
                                + '_' + categoryGroupCode.getCategoryName()
                                + '_' + LocalDate.now() + Constants.CSV_EXTENSION;
        System.out.println("Creating file : " + fileName);
        init(categoryGroupCode, fileName);
        writeCategoryData(extractAreaType, extractAreaType.getRectangleBuilderVo());
        flushGeoDataToCsv(extractAreaType);
        System.out.println("Created file : " + fileName);
        close();
        return fileName;
    }

    private void writeCategoryData(
            ExtractAreaType extractAreaType, RectangleBuilderVo searchZone) throws IOException {
        final LocalSearchRequestVo requestVo = LocalSearchRequestVo.from(
                categoryGroupCode, searchZone, 1, Constants.MAX_PAGE_SIZE);

        LocalCategorySearchResponseDto responseDto;
        try {
            responseDto = searchLocal(KeyStorage.get(), requestVo);
        } catch (Exception e) {
            log.error("Failed request with {}", KeyStorage.get(), e);
            responseDto = retryWithNewKey(requestVo);
        }

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
                writeCategoryData(extractAreaType, newZone);
            }
        }

        if (documents.size() > FLUSH_THRESHOLD) {
            flushGeoDataToCsv(extractAreaType);
        }
    }

    private void addDocumentsToList(LocalCategorySearchResponseDto responseDto, RectangleBuilderVo searchZone) {
        System.out.println("[" + categoryGroupCode + "] Saving data from zone : "
                           + searchZone.getRectString());
        addAll(responseDto.getDocuments());
        if (!responseDto.getMeta().isEnd()) {
            final List<LocalDocumentDto> restOfDocuments = retrieveRestOfLocalDocuments(
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

    private void flushGeoDataToCsv(ExtractAreaType extractAreaType) throws IOException {
        ListTransformer.transform(documents, GeoDataVo::from).stream()
                       .filter(geoDataVo -> extractAreaType.getAreaName().isEmpty()
                                            || geoDataVo.getCity().equals(extractAreaType.getAreaName()))
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

    private LocalCategorySearchResponseDto retryWithNewKey(LocalSearchRequestVo requestVo) {
        KeyStorage.deactivateKey(KeyStorage.get());
        while(KeyStorage.hasActiveKey()) {
            try {
                return searchLocal(KeyStorage.get(), requestVo);
            } catch (Exception e) {
                log.error("Failed request with key : {}", KeyStorage.get() , e);
                KeyStorage.deactivateKey(KeyStorage.get());
            }
        }
        throw new RuntimeException("No active keys to continue extraction.");
    }



    private List<LocalDocumentDto> retrieveRestOfLocalDocuments(
            CategoryGroupCode categoryGroupCode, RectangleBuilderVo rectangleBuilderVo) {
        final List<LocalDocumentDto> localDocuments = Lists.newArrayList();
        for (int i = 2; i <= Constants.MAX_TOTAL_PAGE; i++) {
            final LocalSearchRequestVo requestVo = LocalSearchRequestVo.from(
                    categoryGroupCode, rectangleBuilderVo, i, Constants.MAX_PAGE_SIZE);
            final LocalCategorySearchResponseDto responseDto = searchLocal(
                    KeyStorage.get(), requestVo);

            localDocuments.addAll(responseDto.getDocuments());
            if (responseDto.getMeta().isEnd()) {
                break;
            }
        }
        return localDocuments;
    }
    @PreDestroy
    public void close() throws IOException {
        idSet.clear();
        documents.clear();
        csvPrinter.close();
    }
}
