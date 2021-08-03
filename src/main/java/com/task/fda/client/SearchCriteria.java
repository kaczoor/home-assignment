package com.task.fda.client;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Data
public class SearchCriteria {

    private final String manufacturerName;
    private final String brandName;

}
