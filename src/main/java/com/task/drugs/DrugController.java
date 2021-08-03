package com.task.drugs;

import com.task.fda.client.BadRequestFdaException;
import com.task.fda.client.FdaException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
@Slf4j
class DrugController {

    private final DrugService service;

    @GetMapping("/drugs")
    public List<DrugDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/drugs/{applicationNumber}")
    public ResponseEntity<DrugDto> findDrug(@PathVariable String applicationNumber) {
        return service.findDrug(applicationNumber).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/drugs")
    @ResponseStatus(HttpStatus.CREATED)
    public DrugDto store(@Valid @RequestBody DrugDto drug) {
        return service.store(drug);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Number of records per page."),
    })
    @GetMapping("/external/drugs")
    public Page<DrugDto> findDrugs(@RequestParam String manufacturerName,
                                   @RequestParam(required = false) String brandName,
                                   @ApiIgnore Pageable pageable) {
        return service.findInFda(manufacturerName, brandName, pageable);
    }

    @ExceptionHandler(FdaException.class)
    public ResponseEntity<Void> handleFdaException(FdaException ex) {
        log.error("Error during executing request to external service", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @ExceptionHandler(BadRequestFdaException.class)
    public ResponseEntity<Void> handleFdaException(BadRequestFdaException ex) {
        log.info("Bad request parameters to external service, message={}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Void> handleDuplicateKey(DuplicateKeyException ex) {
        log.info("Duplicate key occurred, message={}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }


}
