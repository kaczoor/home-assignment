package com.task.fda.client;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FdaClient {

    Optional<DrugResponseDto> findDrugRecords(SearchCriteria criteria, Pageable page);
}
