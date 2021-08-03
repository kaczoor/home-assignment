package com.task.drugs;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@DataMongoTest
class DrugRepositoryIT {

    @Autowired
    private DrugRepository subject;

    @Test
    void returns_empty_when_nothing_is_stored() {
        //when
        List<DrugEntity> actual = subject.findAll();
        //then
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void returns_all_drugs() {
        //given
        DrugEntity entity1 = new DrugEntity();
        entity1.setApplicationNumber("app1");
        entity1.setManufacturerName(Arrays.asList("m1", "m2"));
        entity1.setSubstanceName(Arrays.asList("s1", "s2"));
        entity1.setProducts(
                Arrays.asList(new ProductEntity("no1"), new ProductEntity("no2"))
        );
        subject.save(entity1);
        DrugEntity entity2 = new DrugEntity();
        entity2.setApplicationNumber("app2");
        entity2.setManufacturerName(Arrays.asList("m3", "m4"));
        entity2.setSubstanceName(Arrays.asList("s3", "s4"));
        entity2.setProducts(
                Arrays.asList(new ProductEntity("no3"), new ProductEntity("no4"))
        );
        subject.save(entity2);
        //when
        List<DrugEntity> actual = subject.findAll(Sort.by("applicationNumber"));
        //then
        assertThat(actual).hasSize(2);
        assertEntity(actual.get(0), entity1);
        assertEntity(actual.get(1), entity2);
    }

    @Test
    void throws_exception_when_application_number_is_duplicated(){
        //given
        DrugEntity entity1 = new DrugEntity();
        entity1.setApplicationNumber("app1");
        subject.save(entity1);
        DrugEntity duplicated = new DrugEntity();
        duplicated.setApplicationNumber("app1");
        //when
        Throwable actual = catchThrowable(() -> subject.save(duplicated));
        //then
        assertThat(actual).isExactlyInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void finds_drug_by_application_number(){
        //given
        DrugEntity entity = new DrugEntity();
        entity.setApplicationNumber("app1");
        entity.setManufacturerName(Arrays.asList("m1", "m2"));
        entity.setSubstanceName(Arrays.asList("s1", "s2"));
        entity.setProducts(
                Arrays.asList(new ProductEntity("no1"), new ProductEntity("no2"))
        );
        subject.save(entity);
        //when
        Optional<DrugEntity> actual = subject.findByApplicationNumber("app1");
        //then
        assertThat(actual).hasValueSatisfying(actualEntity -> {
            assertEntity(actualEntity, entity);
        });
    }

    @Test
    void returns_empty_when_drug_does_not_exist_for_application_number(){
        //when
        Optional<DrugEntity> actual = subject.findByApplicationNumber("app1");
        //then
        assertThat(actual).isEmpty();
    }

    @AfterEach
    public void tear_down(){
        subject.deleteAll();
    }

    private void assertEntity(DrugEntity actual, DrugEntity expected) {
        assertThat(actual.getApplicationNumber()).isEqualTo(expected.getApplicationNumber());
        assertThat(actual.getManufacturerName()).isEqualTo(expected.getManufacturerName());
        assertThat(actual.getSubstanceName()).isEqualTo(expected.getSubstanceName());
        assertThat(actual.getProducts()).hasSize(expected.getProducts().size());
        for (int i = 0; i < actual.getProducts().size(); i++) {
            ProductEntity actualProduct = actual.getProducts().get(i);
            ProductEntity expectedProduct = expected.getProducts().get(i);
            assertThat(actualProduct.getProductNumber()).isEqualTo(expectedProduct.getProductNumber());
        }
    }
}