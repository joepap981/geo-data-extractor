package com.joepap.geodataextractor.adapter.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.joepap.geodataextractor.adapter.dto.KakaoLocalType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KakaoLocalTypeSerializer extends JsonSerializer<KakaoLocalType> {
    @Override
    public void serialize(KakaoLocalType value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        log.debug("serialize value : {}" , value.getCode());
        jgen.writeString(value.getCode());
    }
}
