package com.joepap.geodataextractor.controller;

import java.util.List;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.joepap.geodataextractor.service.local.KeyStorage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ShellComponent
public class ApiKeyCommands {

    @ShellMethod(value = "KAKAO REST API키를 신규로 등록한다.", key = "register-key")
    public String registerApiKey(@ShellOption List<String> apiKeys) {
        KeyStorage.add(apiKeys);
        return String.format("Registered REST API KEY: %s", apiKeys);
    }

    @ShellMethod(value = "등록되어 있는 KAKAO REST API키를 조회한다.", key = "list-keys")
    public String listApiKeys() {
        return KeyStorage.printKeys();
    }

    @ShellMethod(value = "등록되어 있는 KAKAO REST API키를 주키로 활성화한다.", key = "activate-key")
    public String activateKey(String apiKey) {
        return KeyStorage.activateKey(apiKey);
    }

    @ShellMethod(value = "등록되어 있는 KAKAO REST API키를 삭제한다.", key = "remove-key")
    public String removeKey(String apiKey) {
        return KeyStorage.remove(apiKey);
    }

}