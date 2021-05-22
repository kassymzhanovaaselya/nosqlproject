package com.recipes.repo;

import com.recipes.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CarRepo extends MongoRepository<Car, String> {
    List<Car> findByNameContaining(String name);
}
