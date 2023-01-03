package com.joepap.geodataextractor.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StopWatch;

import com.google.common.collect.Lists;
import com.joepap.geodataextractor.adapter.dto.CategoryGroupCode;
import com.joepap.geodataextractor.service.local.GeoDataFileCreator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ShellComponent("Geo Data Generate")
@RequiredArgsConstructor
public class GeoDataCreateCommands {
    private final ApplicationContext applicationContext;

    @ShellMethod(key = "list-code", value = "요청 가능한 CategoryGroupCode를 조회한다")
    public String listCategoryGroupCodes() {
        final StringBuilder sb = new StringBuilder();
        for (CategoryGroupCode categoryGroupCode : CategoryGroupCode.values()) {
            sb.append(categoryGroupCode.getCode()).append('[' + categoryGroupCode.getCategoryName() + "]\n");
        }
        sb.replace(sb.length() -1, sb.length(), "");
        return sb.toString();
    }


    @ShellMethod(key = "create", value = "위치 데이터 CSV 파일을 생성한다")
    public String createGeoDataCsvFile(
            @ShellOption(value = "--code", defaultValue = ShellOption.NULL) List<String> codeList,
            @ShellOption(value = "--outputPath", defaultValue = "./data") String outputPath)
            throws IOException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final List<CategoryGroupCode> categoryGroupCodes;
        if (codeList == null) {
            categoryGroupCodes = Arrays.stream(CategoryGroupCode.values()).toList();
        } else {
            categoryGroupCodes = codeList.stream()
                                         .map(CategoryGroupCode::from)
                                         .collect(Collectors.toList());
        }

        final List<String> fileNames = Lists.newArrayList();
        for (CategoryGroupCode categoryGroupCode : categoryGroupCodes) {
            final GeoDataFileCreator geoDataFileCreator = applicationContext.getBean(GeoDataFileCreator.class);
            final String fileName = geoDataFileCreator.createGeoCsvFileForCode(categoryGroupCode, outputPath);
            fileNames.add(fileName);
        }

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeSeconds() + " seconds elapsed.");
        return String.format("Created file(s) : %s", fileNames);
    }
}
