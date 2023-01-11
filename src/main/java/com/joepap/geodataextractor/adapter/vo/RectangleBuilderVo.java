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

    public static RectangleBuilderVo of(Point leftTop, Point rightBottom) {
        return new RectangleBuilderVo(leftTop, rightBottom);
    }

    public String getRectString() {
        return leftTop.lon + DELIMITER + leftTop.lat + DELIMITER +
               rightBottom.lon + DELIMITER + rightBottom.lat;
    }

    public RectangleBuilderVo getLeftTopZone() {
        return new RectangleBuilderVo(
                new Point(leftTop.lon, leftTop.lat),
                new Point((leftTop.lon + rightBottom.lon) / 2, (leftTop.lat + rightBottom.lat) / 2));
    }

    public RectangleBuilderVo getRightTopZone() {
        return new RectangleBuilderVo(
                new Point((leftTop.lon + rightBottom.lon) / 2, leftTop.lat),
                new Point(rightBottom.lon, (leftTop.lat + rightBottom.lat) / 2));
    }

    public RectangleBuilderVo getLeftBottomZone() {
        return new RectangleBuilderVo(
                new Point(leftTop.lon, (leftTop.lat + rightBottom.lat) / 2),
                new Point((leftTop.lon + rightBottom.lon) / 2, rightBottom.lat));
    }

    public RectangleBuilderVo getRightBottomZone() {
        return new RectangleBuilderVo(
                new Point((leftTop.lon + rightBottom.lon) / 2, (leftTop.lat + rightBottom.lat) / 2),
                new Point(rightBottom.lon, rightBottom.lat));
    }

    public record Point (double lon, double lat) {
        public static Point getMiddlePoint(Point a, Point b) {
            return new Point((a.lon + b.lon) / 2, (a.lat + b.lat) / 2);
        }
    }

    public boolean isLessThanMinDegree(double minDegreeDifference) {
        return rightBottom.lon - leftTop.lon < minDegreeDifference
                || leftTop.lat - rightBottom.lat < minDegreeDifference;
    }
}