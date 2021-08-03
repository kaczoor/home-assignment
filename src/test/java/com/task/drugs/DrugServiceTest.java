package com.task.drugs;


import com.task.fda.client.DrugResponseDto;
import com.task.fda.client.FdaClient;
import com.task.fda.client.FdaProductDto;
import com.task.fda.client.MetaDto;
import com.task.fda.client.MetaResultDto;
import com.task.fda.client.OpenFdaDto;
import com.task.fda.client.ResultDto;
import com.task.fda.client.SearchCriteria;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DrugServiceTest {

    private final DrugRepository drugRepository = mock(DrugRepository.class);
    private final FdaClient fdaClient = mock(FdaClient.class);
    private final DrugService subject = new DrugConfiguration().drugService(drugRepository, fdaClient);

    @Test
    void returns_empty_page_when_drugs_does_not_exist_in_fda() {
        //given
        SearchCriteria criteria = SearchCriteria.builder().brandName("brand").manufacturerName("manufacturer").build();
        when(fdaClient.findDrugRecords(criteria, PageRequest.ofSize(1))).thenReturn(Optional.empty());
        //when
        Page<DrugDto> actual = subject.findInFda("manufacturer", "brand", PageRequest.ofSize(1));
        //then
        assertThat(actual).isEmpty();
        assertThat(actual.getPageable()).isEqualTo(PageRequest.ofSize(1));
    }

    @Test
    void returns_paged_data_when_drugs_exists_in_fda() {
        //given
        SearchCriteria criteria = SearchCriteria.builder().brandName("brand").manufacturerName("manufacturer").build();
        DrugResponseDto response = new DrugResponseDto();
        response.setMeta(new MetaDto(new MetaResultDto(100)));
        response.setResults(Collections.singletonList(
                new ResultDto("appNumber",
                        new OpenFdaDto(Collections.emptyList(),
                                Arrays.asList("manufacturer1", "manufacturer2"),
                                Arrays.asList("substance1", "substance2")),
                        Arrays.asList(
                                new FdaProductDto("number1", "drug1", "band1"),
                                new FdaProductDto("number2", "drug2", "band2")
                        ))
        ));
        when(fdaClient.findDrugRecords(criteria, PageRequest.ofSize(1))).thenReturn(Optional.of(response));
        //when
        Page<DrugDto> actual = subject.findInFda("manufacturer", "brand", PageRequest.ofSize(1));
        //then
        assertThat(actual.getPageable()).isEqualTo(PageRequest.ofSize(1));
        assertThat(actual.getTotalElements()).isEqualTo(100);
        assertThat(actual.getContent()).hasSize(1).containsExactly(
                new DrugDto("appNumber",
                        Arrays.asList("manufacturer1", "manufacturer2"),
                        Arrays.asList("substance1", "substance2"),
                        Arrays.asList(
                                new ProductDto("number1"),
                                new ProductDto("number2")
                        )
                )
        );
    }
}