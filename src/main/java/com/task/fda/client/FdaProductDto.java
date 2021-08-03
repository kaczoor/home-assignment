package com.task.fda.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FdaProductDto {

    @JsonProperty("product_number")
    private String productNumber;

    @JsonProperty("reference_drug")
    private String referenceDrug;

    @JsonProperty("brand_name")
    private String brandName;
}
