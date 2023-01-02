package com.joepap.geodataextractor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class GeoDataExtractorApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = new SpringApplicationBuilder(
                GeoDataExtractorApplication.class).web(WebApplicationType.NONE).run(args);
        final int exitCode = SpringApplication.exit(context);
        context.close();
        System.exit(exitCode);
    }
}
