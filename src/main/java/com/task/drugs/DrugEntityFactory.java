package com.task.drugs;

import java.util.stream.Collectors;

class DrugEntityFactory {


    DrugEntity create(DrugDto drug){
        DrugEntity entity = new DrugEntity();
        entity.setApplicationNumber(drug.getApplicationNumber());
        entity.setProducts(drug.getProducts().stream().map(product -> new ProductEntity(product.getProductNumber())).collect(Collectors.toList()));
        entity.setSubstanceName(drug.getSubstanceName());
        entity.setManufacturerName(drug.getManufacturerName());
        return entity;
    }
}
