package com.task.fda.client.rest;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties("task.fda")
@Component
class FdaProperties {

    private String url = "https://api.fda.gov";
    private int connectionTimeoutInMs = 1000;
    private int maxConnections = 10;
    private int readTimeoutInMs = 1000;
}
