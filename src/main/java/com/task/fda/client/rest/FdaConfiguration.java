package com.task.fda.client.rest;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
class FdaConfiguration {

    @Bean
    RestFdaClient restFdaClient(FdaProperties fdaProperties, RestTemplate fdaRestTemplate) {
        return new RestFdaClient(fdaProperties.getUrl(), fdaRestTemplate);
    }

    @Bean
    RestTemplate fdaRestTemplate(FdaProperties fdaProperties) {
        ClientHttpRequestFactory factory = createHttpFactory(fdaProperties);
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setErrorHandler(new FdaResponseErrorHandler());
        return restTemplate;
    }

    private static ClientHttpRequestFactory createHttpFactory(FdaProperties fdaProperties) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = createHttpClient(fdaProperties);
        factory.setHttpClient(httpClient);
        return factory;
    }

    private static HttpClient createHttpClient(FdaProperties fdaProperties) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(fdaProperties.getMaxConnections());
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(fdaProperties.getConnectionTimeoutInMs())
                .setConnectionRequestTimeout(fdaProperties.getConnectionTimeoutInMs())
                .setSocketTimeout(fdaProperties.getReadTimeoutInMs())
                .build();
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
    }
}
