package com.task.drugs;


import com.task.fda.client.DrugResponseDto;
import com.task.fda.client.FdaClient;
import com.task.fda.client.SearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class DrugService {

    private final DrugRepository drugRepository;
    private final FdaClient fdaClient;
    private final DrugEntityFactory factory;
    private final DrugMapper mapper;

    Page<DrugDto> findInFda(String manufacturerName, String brandName, Pageable pageable){
        SearchCriteria search = SearchCriteria.builder().manufacturerName(manufacturerName).brandName(brandName).build();
       return fdaClient.findDrugRecords(search, pageable)
               .map(response -> mapToPage(pageable, response))
               .orElseGet(() -> Page.empty(pageable));
    }

    private Page<DrugDto> mapToPage(Pageable pageable, DrugResponseDto response){
        List<DrugDto> drugs = response.getResults().stream().map(mapper::map).collect(Collectors.toList());
        return new PageImpl<>(drugs, pageable, response.getMeta().getResults().getTotal());
    }

    public List<DrugDto> findAll() {
        return drugRepository.findAll(Sort.by("applicationNumber")).stream().map(mapper::map).collect(Collectors.toList());
    }

    public Optional<DrugDto> findDrug(String applicationNumber) {
        return drugRepository.findByApplicationNumber(applicationNumber).map(mapper::map);
    }

    public DrugDto store(DrugDto drug) {
        DrugEntity entity = factory.create(drug);
        DrugEntity saved = drugRepository.save(entity);
        return mapper.map(saved);
    }
}
