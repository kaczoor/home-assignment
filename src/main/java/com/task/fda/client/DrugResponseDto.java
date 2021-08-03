package com.task.fda.client;

import lombok.Data;

import java.util.List;

@Data
public class DrugResponseDto {

    private MetaDto meta;
    private List<ResultDto> results;

}
