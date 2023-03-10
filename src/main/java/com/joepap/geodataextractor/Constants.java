package com.joepap.geodataextractor;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final int MAX_PAGE_SIZE = 15;
    public static final int MAX_TOTAL_PAGE = 45;

    public static final double SEOUL_MIN_LONGITUDE = 126.7630;
    public static final double SEOUL_MIN_LATITUDE = 37.4280;
    public static final double SEOUL_MAX_LONGITUDE = 127.1854;
    public static final double SEOUL_MAX_LATITUDE = 37.7020;
    public static final String CSV_EXTENSION = ".csv";
    public static final long MOCK_DATA_ID = -1;
}
