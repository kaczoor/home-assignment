package com.task.fda.client.rest;


import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.task.fda.client.BadRequestFdaException;
import com.task.fda.client.DrugResponseDto;
import com.task.fda.client.FdaException;
import com.task.fda.client.FdaProductDto;
import com.task.fda.client.MetaDto;
import com.task.fda.client.OpenFdaDto;
import com.task.fda.client.ResultDto;
import com.task.fda.client.SearchCriteria;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class RestFdaClientIT {

    private RestFdaClient subject;
    private WireMockServer wireMockServer;

    @BeforeEach
    void set_up() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options()
                .usingFilesUnderClasspath("src/test/resources//wiremock/fda")
                .dynamicPort());
        wireMockServer.start();
        FdaConfiguration configuration = new FdaConfiguration();
        FdaProperties properties = new FdaProperties();
        properties.setUrl(wireMockServer.baseUrl());
        RestTemplate restTemplate = configuration.fdaRestTemplate(properties);
        subject = configuration.restFdaClient(properties, restTemplate);
    }

    @AfterEach
    void tear_down() {
        wireMockServer.stop();
    }

    @Test
    void returns_empty_when_criteria_does_not_match_any_data() {
        //given
        wireMockServer.stubFor(get(urlEqualTo("/drug/drugsfda.json?search=openfda.manufacturer_name.exact:%22test%22&limit=1&skip=0"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":{\"code\":\"NOT_FOUND\",\"message\":\"No matches found!\"}}")));
        //when
        Optional<DrugResponseDto> actual = subject.findDrugRecords(SearchCriteria.builder().manufacturerName("test").build(), PageRequest.ofSize(1));
        //then
        assertThat(actual).isEmpty();
    }

    @Test
    void returns_drug_data_when_manufacturer_name_is_matched() {
        //given
        wireMockServer.stubFor(get(
                urlEqualTo("/drug/drugsfda.json?search=openfda.manufacturer_name.exact:%22Lupin%20Pharmaceuticals,%20Inc.%22&limit=2&skip=0"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("manufacturer_name_matched.json")));
        //when
        Optional<DrugResponseDto> actual = subject.findDrugRecords(SearchCriteria
                .builder()
                .manufacturerName("Lupin Pharmaceuticals, Inc.")
                .build(), PageRequest.ofSize(2));
        //then
        assertThat(actual).hasValueSatisfying(response -> {
            assertThat(response.getResults()).hasSize(2);
            for (ResultDto result : response.getResults()) {
                assertThat(result.getOpenFda().getManufacturerName()).containsExactly("Lupin Pharmaceuticals, Inc.");
            }
        });
    }

    @Test
    void returns_drug_data_when_brand_name_is_matched() {
        //given
        wireMockServer.stubFor(get(
                urlEqualTo("/drug/drugsfda.json?search=openfda.brand_name.exact:%22RANOLAZINE%22&limit=2&skip=0"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("brand_name_matched.json")));
        //when
        Optional<DrugResponseDto> actual = subject.findDrugRecords(SearchCriteria.builder().brandName("RANOLAZINE").build(), PageRequest.ofSize(2));
        //then
        assertThat(actual).hasValueSatisfying(response -> {
            assertThat(response.getResults()).hasSize(2);
            for (ResultDto result : response.getResults()) {
                assertThat(result.getOpenFda().getBrandName()).containsExactly("RANOLAZINE");
            }
        });
    }

    @Test
    void returns_data_from_next_page() {
        //given
        wireMockServer.stubFor(get(
                urlEqualTo("/drug/drugsfda.json?limit=1&skip=2"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"meta\":{\"results\":{\"skip\":2,\"limit\":1,\"total\":24987}},\"results\":[{\"products\":[{\"product_number\":\"001\",\"reference_drug\":\"No\",\"brand_name\":\"LAPATINIB DITOSYLATE\"}]}]}")));
        //when
        Optional<DrugResponseDto> actual = subject.findDrugRecords(SearchCriteria.builder().build(), PageRequest.of(2, 1));
        //then
        assertThat(actual).hasValueSatisfying(response -> {
            assertThat(response.getResults()).hasSize(1);
        });
    }

    @Test
    void throws_general_exception_when_fda_returns_server_error() {
        //given
        wireMockServer.stubFor(get(urlEqualTo("/drug/drugsfda.json?limit=1&skip=0"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        //when
        Throwable actual = catchThrowable(() -> subject.findDrugRecords(SearchCriteria.builder().build(), PageRequest.ofSize(1)));
        //then
        assertThat(actual).isExactlyInstanceOf(FdaException.class);
    }

    @Test
    void throws_bad_request_exception_when_request_is_invalid(){
        //given
        wireMockServer.stubFor(get(urlEqualTo("/drug/drugsfda.json?limit=25001&skip=0"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withBody("{\n" +
                                "  \"error\": {\n" +
                                "    \"code\": \"BAD_REQUEST\",\n" +
                                "    \"message\": \"Skip value must 25000 or less.\"\n" +
                                "  }\n" +
                                "}\n")));
        //when
        Throwable actual = catchThrowable(() -> subject.findDrugRecords(SearchCriteria.builder().build(), PageRequest.ofSize(25001)));
        //then
        assertThat(actual).isExactlyInstanceOf(BadRequestFdaException.class);
    }

    @Test
    void returns_all_data_from_fda() {
        //given
        wireMockServer.stubFor(get(
                urlEqualTo("/drug/drugsfda.json?limit=2&skip=0"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("manufacturer_name_matched.json")));
        //when
        Optional<DrugResponseDto> actual = subject.findDrugRecords(SearchCriteria
                .builder()
                .build(), PageRequest.ofSize(2));
        //then
        assertThat(actual).hasValueSatisfying(response -> {
            assertTotalSize(response.getMeta());
            assertResults(response.getResults());
        });
    }

    @Test
    void returns_data_when_all_criteria_are_matched() {
        //given
        wireMockServer.stubFor(get(
                urlEqualTo("/drug/drugsfda.json?search=openfda.manufacturer_name.exact:%22Lupin%20Pharmaceuticals,%20Inc.%22+AND+openfda.brand_name.exact:%22RANOLAZINE%22&limit=1&skip=0"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("all_criteria_matched.json")));
        //when
        Optional<DrugResponseDto> actual = subject.findDrugRecords(SearchCriteria
                .builder()
                .brandName("RANOLAZINE")
                .manufacturerName("Lupin Pharmaceuticals, Inc.")
                .build(), PageRequest.ofSize(1));
        //then
        assertThat(actual).hasValueSatisfying(response -> {
            assertThat(response.getResults()).hasSize(1);
            for (ResultDto result : response.getResults()) {
                assertThat(result.getOpenFda().getManufacturerName()).containsExactly("Lupin Pharmaceuticals, Inc.");
                assertThat(result.getOpenFda().getBrandName()).containsExactly("RANOLAZINE");
            }
        });
    }

    private void assertTotalSize(MetaDto actual) {
        int expectedTotalSize = 196;
        assertThat(actual.getResults().getTotal()).isEqualTo(expectedTotalSize);
    }

    private void assertResults(List<ResultDto> actual) {
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0)).satisfies(result -> {
            assertResult(result, new OpenFdaDto(
                            Arrays.asList("LAPATINIB"),
                            Arrays.asList("Lupin Pharmaceuticals, Inc."),
                            Arrays.asList("LAPATINIB DITOSYLATE")),
                    FdaProductDto
                            .builder()
                            .productNumber("001")
                            .brandName("LAPATINIB DITOSYLATE")
                            .referenceDrug("No")
                            .build());
        });
        assertThat(actual.get(1)).satisfies(result -> {
            assertResult(result,
                    new OpenFdaDto(
                            Arrays.asList("RANOLAZINE"),
                            Arrays.asList("Lupin Pharmaceuticals, Inc."),
                            Arrays.asList("RANOLAZINE")),
                    FdaProductDto
                            .builder()
                            .productNumber("001")
                            .brandName("RANOLAZINE")
                            .referenceDrug("No")
                            .build());
        });
    }


    private void assertResult(ResultDto actual, OpenFdaDto expectedOpenFda,
                              FdaProductDto expectedProduct) {
        assertThat(actual.getOpenFda()).isEqualTo(expectedOpenFda);
        assertThat(actual.getProducts()).satisfies(product -> {
            assertThat(product).isEqualTo(expectedProduct);
        }, Index.atIndex(0));
    }

}