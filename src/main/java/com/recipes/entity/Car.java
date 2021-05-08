package com.recipes.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "cars")
public class Car {
    @Id
    private String id;

    private String name;
    private String year;
    private String selling_price;
    private String km_driven;
    private String fuel;
    private String seller_type;
    private String transmission;
    private String owner;


}
