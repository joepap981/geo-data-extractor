package com.joepap.geodataextractor.controller;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.joepap.geodataextractor.service.local.KeyStorage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ShellComponent
public class ApiKeyCommands {

    @ShellMethod(value = "KAKAO REST API키를 신규로 등록한다.", key = "register-key")
    public String registerApiKey(@ShellOption String apiKey) {
        KeyStorage.set(apiKey);
        return String.format("Registered REST API KEY: %s", apiKey);
    }

    @ShellMethod(value = "등록되어 있는 KAKAO REST API키를 조회한다.", key = "view-key")
    public String retrieveApiKey() {
        return String.format("Current REST API KEY: %s", KeyStorage.get());
    }
}