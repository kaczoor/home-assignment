package com.task.drugs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
class DrugDto {

    @NotBlank
    private String applicationNumber;
    @NotEmpty
    @NotNull
    private List<String> manufacturerName = Collections.emptyList();
    @NotNull
    private List<String> substanceName = Collections.emptyList();
    @NotNull
    private List<ProductDto> products = Collections.emptyList();
}
