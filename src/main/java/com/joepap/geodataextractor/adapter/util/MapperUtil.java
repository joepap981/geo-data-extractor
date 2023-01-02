package com.joepap.geodataextractor.adapter.util;

import java.util.Map;

import org.apache.logging.log4j.util.Strings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Maps;

public class MapperUtil {
    private static ObjectMapper objectMapper;

    public static ObjectMapper OBJECT_MAPPER() {
        initObjectMapper();
        return objectMapper;
    }

    public static Map<String, Object> toMap(Object o) {
        if (o == null) {
            return Maps.newHashMap();
        }
        initObjectMapper();
        return objectMapper.convertValue(o, new TypeReference<>() {});
    }

    public static Map<String, String> toStringMap(Object o) {
        if (o == null) {
            return Maps.newHashMap();
        }
        initObjectMapper();
        return objectMapper.convertValue(o, new TypeReference<>() {});
    }

    public static String toJSONString(Object o) throws JsonProcessingException {
        if (o == null) {
            return Strings.EMPTY;
        }
        initObjectMapper();
        return objectMapper.writeValueAsString(o);
    }

    private static void initObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        }
    }
}
