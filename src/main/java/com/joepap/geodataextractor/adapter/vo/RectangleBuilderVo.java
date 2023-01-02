package com.joepap.geodataextractor.adapter.vo;

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

    public record Point (double lon, double lat) {
        public static Point getMiddlePoint(Point a, Point b) {
            return new Point((a.lon + b.lon) / 2, (a.lat + b.lat) / 2);
        }
    }

    public String getRectString() {
        return leftTop.lon + DELIMITER + leftTop.lat + DELIMITER +
               rightBottom.lon + DELIMITER + rightBottom.lat;
    }
}
