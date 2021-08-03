package com.task.drugs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "drugs")
class DrugEntity {

    @Id
    private String id;
    @Indexed(unique = true)
    private String applicationNumber;
    private List<String> manufacturerName;
    private List<String> substanceName;
    private List<ProductEntity> products;
}
