package com.task.fda.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto {
    @JsonProperty("application_number")
    private String applicationNumber;
    @JsonProperty("openfda")
    private OpenFdaDto openFda;
    private List<FdaProductDto> products;
}
