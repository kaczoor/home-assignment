package com.task.fda.client.rest;

import com.task.fda.client.DrugResponseDto;
import com.task.fda.client.FdaClient;
import com.task.fda.client.SearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
class RestFdaClient implements FdaClient {

    private static final String CRITERIA_DELIMITER = "+AND+";
    private static final String TERM_DELIMITER = ":";
    private static final String DRUG_ENDPOINT = "/drug/drugsfda.json";
    private final String url;
    private final RestTemplate restTemplate;

    @Override
    public Optional<DrugResponseDto> findDrugRecords(SearchCriteria criteria, Pageable page) {
        URI searchUri = createSearchUri(criteria, page);
        ResponseEntity<DrugResponseDto> response = restTemplate.getForEntity(searchUri, DrugResponseDto.class);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return Optional.empty();
        }
        DrugResponseDto body = response.getBody();
        return Optional.ofNullable(body);
    }

    private URI createSearchUri(SearchCriteria criteria, Pageable page){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).path(DRUG_ENDPOINT);
        createSearch(criteria).ifPresent(search -> builder.queryParam("search", search));
        return builder
                .queryParam("limit", page.getPageSize())
                .queryParam("skip", page.getPageSize() * page.getOffset())
                .build().toUri();
    }

    private static Optional<String> createSearch(SearchCriteria criteria) {
        List<String> search = new ArrayList<>();
        getCriteria("openfda.manufacturer_name.exact", criteria.getManufacturerName()).ifPresent(search::add);
        getCriteria("openfda.brand_name.exact", criteria.getBrandName()).ifPresent(search::add);
        if (search.isEmpty()) {
            return Optional.empty();
        }
        String searchCriteria = String.join(CRITERIA_DELIMITER, search);
        return Optional.of(searchCriteria);
    }

    private static Optional<String> getCriteria(String name, String value){
        if (nonNull(value) && !value.isEmpty()) {
            String criteria = name + TERM_DELIMITER + wrap(value);
            return Optional.of(criteria);
        }
        return Optional.empty();
    }

    private static String wrap(String value){
        return "\"" + value +"\"";
    }

}
