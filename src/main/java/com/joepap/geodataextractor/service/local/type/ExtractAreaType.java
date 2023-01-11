package com.joepap.geodataextractor.service.local.type;

import org.apache.logging.log4j.util.Strings;

import com.joepap.geodataextractor.Constants;
import com.joepap.geodataextractor.adapter.vo.RectangleBuilderVo;
import com.joepap.geodataextractor.adapter.vo.RectangleBuilderVo.Point;

import lombok.Getter;

@Getter
public enum ExtractAreaType {
    KOREA(Strings.EMPTY,
            new RectangleBuilderVo(
            new Point(124.78368123, 38.62213667),
            new Point(131.01312934, 33.06295304)
    )),
    SEOUL("서울",
            new RectangleBuilderVo(
            new Point(Constants.SEOUL_MIN_LONGITUDE, Constants.SEOUL_MAX_LATITUDE),
            new Point(Constants.SEOUL_MAX_LONGITUDE, Constants.SEOUL_MIN_LATITUDE)
    ));

    private final String areaName;
    private final RectangleBuilderVo rectangleBuilderVo;

    ExtractAreaType(String areaName, RectangleBuilderVo rectangleBuilderVo) {
        this.areaName = areaName;
        this.rectangleBuilderVo = rectangleBuilderVo;
    }
}
