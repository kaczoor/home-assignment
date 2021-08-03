package com.task.drugs;

import com.task.fda.client.FdaProductDto;
import com.task.fda.client.ResultDto;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class DrugMapper {

    DrugDto map(ResultDto result) {
        return new DrugDto(
                result.getApplicationNumber(),
                result.getOpenFda().getManufacturerName(),
                result.getOpenFda().getSubstanceName(),
                mapProducts(result.getProducts(), FdaProductDto::getProductNumber)
        );
    }

    DrugDto map(DrugEntity drugEntity) {
        return new DrugDto(
                drugEntity.getApplicationNumber(),
                drugEntity.getManufacturerName(),
                drugEntity.getSubstanceName(),
                mapProducts(drugEntity.getProducts(), ProductEntity::getProductNumber)
        );
    }

    private static <T> List<ProductDto> mapProducts(List<T> products, Function<T, String> getter) {
        return products.stream().map(product -> new ProductDto(getter.apply(product))).collect(Collectors.toList());
    }
}
