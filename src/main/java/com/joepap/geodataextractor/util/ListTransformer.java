package com.joepap.geodataextractor.util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ListTransformer {
    public static <T, R> List<R> transform(List<T> list, Function<T, R> converter) {
        return Optional.ofNullable(list)
                       .orElseGet(Collections::emptyList)
                       .stream()
                       .map(converter)
                       .collect(Collectors.toList());
    }
}

