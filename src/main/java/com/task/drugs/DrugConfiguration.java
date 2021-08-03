package com.task.drugs;

import com.task.fda.client.FdaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DrugConfiguration {


    @Bean
    DrugService drugService(DrugRepository drugRepository, FdaClient fdaClient) {
        return new DrugService(drugRepository, fdaClient, new DrugEntityFactory(), new DrugMapper());
    }
}
