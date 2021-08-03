package com.task.fda.client.rest;

import com.task.fda.client.BadRequestFdaException;
import com.task.fda.client.FdaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
class FdaResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return !response.getStatusCode().is2xxSuccessful() && response.getStatusCode() != HttpStatus.NOT_FOUND;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String body = readStream(response.getBody());
        if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            throw BadRequestFdaException.ofRawBody(body);
        }
        throw FdaException.generalException(response.getRawStatusCode(), body);
    }

    private String readStream(InputStream is) {
        try (is) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Error while reading response body", e);
            return "";
        }
    }
}
