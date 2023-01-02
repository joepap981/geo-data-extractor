package com.joepap.geodataextractor.adapter;

import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.codec.http.HttpScheme;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

@Slf4j
public class AbstractAdapter {

    private static final String DEFAULT_SCHEME = "https";
    protected <T> T put(String uri,
                        ParameterizedTypeReference<T> resultType,
                        HttpHeaders httpHeaders,
                        Object body) {
        log.debug("call put : uri {}", uri);
        return Objects.requireNonNull(WebClient.create(uri)
                                               .put()
                                               .headers(headers -> headers.addAll(httpHeaders))
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .body(BodyInserters.fromValue(body))
                                               .retrieve().toEntity(resultType).block())
                      .getBody();
    }

    protected <T> T get(String uri,
                        ParameterizedTypeReference<T> resultType,
                        HttpHeaders httpHeaders,
                        MultiValueMap<String, String> requestParameters) {
        final WebClient webClient = WebClient.builder()
                                             .defaultHeaders(httpHeaders1 -> httpHeaders1.addAll(httpHeaders))
                                             .baseUrl(uri)
                                             .build();
        return Objects.requireNonNull(webClient.get()
                                               .uri(uriBuilder -> uriBuilder
                                                       .replaceQueryParams(requestParameters)
                                                       .build())
                                               .retrieve().toEntity(resultType).block())
                      .getBody();
    }
}
