package com.task.drugs;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrugRepository extends MongoRepository<DrugEntity, String> {

    Optional<DrugEntity> findByApplicationNumber(String applicationNumber);
}
