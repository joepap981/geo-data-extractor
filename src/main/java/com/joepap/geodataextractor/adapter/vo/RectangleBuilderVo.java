package com.joepap.geodataextractor.adapter.vo;

import com.joepap.geodataextractor.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class RectangleBuilderVo {
    private Point leftTop;
    private Point rightBottom;

    private static final String DELIMITER = ",";

    public static RectangleBuilderVo seoul() {
        return new RectangleBuilderVo(
                new Point(Constants.SEOUL_MIN_LONGITUDE, Constants.SEOUL_MAX_LATITUDE),
                new Point(Constants.SEOUL_MAX_LONGITUDE, Constants.SEOUL_MIN_LATITUDE)
        );
    }
    public static RectangleBuilderVo of(Point leftTop, Point rightBottom) {
        return new RectangleBuilderVo(leftTop, rightBottom);
    }

    public String getRectString() {
        return leftTop.lon + DELIMITER + leftTop.lat + DELIMITER +
               rightBottom.lon + DELIMITER + rightBottom.lat;
    }

    public RectangleBuilderVo getLeftTop() {
        return new RectangleBuilderVo(
                new Point(leftTop.lon, leftTop.lat),
                new Point((leftTop.lon + rightBottom.lon) / 2, (leftTop.lat + rightBottom.lat) / 2));
    }

    public RectangleBuilderVo getRightTop() {
        return new RectangleBuilderVo(
                new Point((leftTop.lon + rightBottom.lon) / 2, leftTop.lat),
                new Point(rightBottom.lon, (leftTop.lat + rightBottom.lat) / 2));
    }

    public RectangleBuilderVo getLeftBottom() {
        return new RectangleBuilderVo(
                new Point(leftTop.lon, (leftTop.lat + rightBottom.lat) / 2),
                new Point((leftTop.lon + rightBottom.lon) / 2, rightBottom.lat));
    }

    public RectangleBuilderVo getRightBottom() {
        return new RectangleBuilderVo(
                new Point((leftTop.lon + rightBottom.lon) / 2, (leftTop.lat + rightBottom.lat) / 2),
                new Point(rightBottom.lon, rightBottom.lat));
    }

    public record Point (double lon, double lat) {
        public static Point getMiddlePoint(Point a, Point b) {
            return new Point((a.lon + b.lon) / 2, (a.lat + b.lat) / 2);
        }
    }

}
