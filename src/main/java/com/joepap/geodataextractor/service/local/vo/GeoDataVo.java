package com.joepap.geodataextractor.service.local.vo;

import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.logging.log4j.util.Strings;

import com.joepap.geodataextractor.Constants;
import com.joepap.geodataextractor.adapter.dto.LocalDocumentDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class GeoDataVo {
    private Long id;
    private String categoryGroupCode;
    private String categoryGroupName;
    private String placeName;
    private String city;
    private String address;
    private String roadAddress;
    private String administrativeRegionName;
    private String administrativeRegionCode;
    private Double longitude;
    private Double latitude;
    private String subCategory1;
    private String subCategory2;
    private String subCategory3;
    private String subCategory4;
    private boolean isMockData;

    private static final String CATEGORY_NAME_DELIMITER = ">";
    private static final String[] HEADERS = {
            "id",
            "categoryGroupCode",
            "categoryGroupName",
            "placeName",
            "city",
            "address",
            "roadAddress",
            "administrativeRegionName",
            "administrativeRegionCode",
            "longitude",
            "latitude",
            "subCategory1",
            "subCategory2",
            "subCategory3",
            "subCategory4",
            "isMockData"
    };

    public static CSVFormat getCsvFormat() {
        final CSVFormat.Builder builder = CSVFormat.DEFAULT.builder();
        builder.setHeader(HEADERS);
        return builder.build();
    }

    public static GeoDataVo from(LocalDocumentDto localDocumentDto) {
        final String[] subCategories = new String[4];
        final String[] delimitedList = Optional.ofNullable(localDocumentDto.getCategoryName())
                                               .map(categoryName -> categoryName.split(CATEGORY_NAME_DELIMITER))
                                               .orElse(new String[]{});


        for (int i = 0; i < delimitedList.length && i < 4; i++) {
            subCategories[i] = delimitedList[i].trim();
        }

        final String address = localDocumentDto.getAddressName();

        return builder()
                .id(localDocumentDto.getId())
                .categoryGroupName(localDocumentDto.getCategoryGroupCode().getCategoryName())
                .categoryGroupCode(localDocumentDto.getCategoryGroupCode().getCode())
                .placeName(localDocumentDto.getPlaceName())
                .city(address != null ? address.split(" ")[0] : Strings.EMPTY)
                .address(address)
                .roadAddress(localDocumentDto.getRoadAddressName())
                .administrativeRegionName(Strings.EMPTY)
                .administrativeRegionCode(Strings.EMPTY)
                .longitude(localDocumentDto.getX())
                .latitude(localDocumentDto.getY())
                .subCategory1(subCategories[0])
                .subCategory2(subCategories[1])
                .subCategory3(subCategories[2])
                .subCategory4(subCategories[3])
                .isMockData(localDocumentDto.getId() == Constants.MOCK_DATA_ID)
                .build();
    }
}
