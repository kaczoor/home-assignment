package com.task.fda.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenFdaDto {

    @JsonProperty("brand_name")
    private List<String> brandName;
    @JsonProperty("manufacturer_name")
    private List<String> manufacturerName;
    @JsonProperty("substance_name")
    private List<String> substanceName;
}
