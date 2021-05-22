package com.recipes.repo;

import com.recipes.entity.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhotoRepo extends MongoRepository<Photo, String> { }

