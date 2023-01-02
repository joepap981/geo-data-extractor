package com.joepap.geodataextractor.service.local.vo;

import org.apache.logging.log4j.util.Strings;

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

    private static final String CATEGORY_NAME_DELIMITER = ">";
    public static final String HEADERS = "";

    public static GeoDataVo from(LocalDocumentDto localDocumentDto) {
        final String[] subCategories = new String[4];
        final String[] delimitedList = localDocumentDto.getCategoryName().split(CATEGORY_NAME_DELIMITER);

        for (int i = 0; i < delimitedList.length && i < 4; i++) {
            subCategories[i] = delimitedList[i].trim();
        }

        final String address = localDocumentDto.getAddressName();

        return builder()
                .id(localDocumentDto.getId())
                .categoryGroupName(localDocumentDto.getCategoryGroupCode().getCategoryName())
                .categoryGroupCode(localDocumentDto.getCategoryGroupCode().getCode())
                .placeName(localDocumentDto.getPlaceName())
                .city(address != null ? address.split(" ")[0] : null)
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
                .build();
    }
}
