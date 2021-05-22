package com.recipes.service;

import com.recipes.entity.Car;
import com.recipes.entity.Params;
import com.recipes.repo.CarRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@AllArgsConstructor
public class CarService {
    @Autowired
    CarRepo carRepo;

    private final MongoTemplate mongoTemplate;

    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        try {
            Car c = carRepo.save(car);
            return new ResponseEntity<>(c, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<List<Car>> getAllCars(@RequestParam(required = false) String name) {
        try {
            List<Car> cars = new ArrayList<Car>();

            if (name == null)
                carRepo.findAll().forEach(cars::add);
            else
                carRepo.findByNameContaining(name).forEach(cars::add);

            if (cars.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(cars, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    public void sort(){
//        mongoTemplate.indexOps(Car.class).ensureIndex(new Index().on("name", Sort.Direction.ASC));
//        System.out.println(mongoTemplate.indexOps("cars").getIndexInfo());
//    }

    public List<Car> search(Params params) {
        try {
            if (params != null){
                MatchOperation matchOperation = null;
                ProjectionOperation projectionOperation = null;
                Criteria criteria1 = new Criteria();
                Aggregation aggregation;
                List<Criteria> criteria = new ArrayList<>();

                if (params.getKeyword() != null && !params.getKeyword().trim().isEmpty()){
                    criteria.add(new Criteria().orOperator(criteriaByRegex("name", params.getKeyword()),
                                                            criteriaByRegex("fuel", params.getKeyword()),
                                                            criteriaByRegex("transmission", params.getKeyword()),
                                                            criteriaByRegex("owner", params.getKeyword()),
                                                            new Criteria("year").is(params.getKeyword()),
                                                            new Criteria("selling_price").is(params.getKeyword()),
                                                            new Criteria("km_driven").is(params.getKeyword())));

                }
                if (params.getFields() != null && !params.getFields().isEmpty() && !params.getFields().contains("all")){
                    projectionOperation = project(params.getFields().toArray(new String[0]));
                }
                if (criteria.size() != 0)
                    criteria1 = new Criteria().andOperator(criteria.toArray(new Criteria[0]));

                matchOperation = new MatchOperation(criteria1);
                if (projectionOperation == null)
                    aggregation = newAggregation(matchOperation);
                else
                    aggregation = newAggregation(matchOperation, projectionOperation);

                System.out.println("agg " + aggregation.toString());

//                System.out.println("m + " + matchOperation.toString());
//                System.out.println("p + " + projectionOperation);
//                System.out.println("c + " + criteria1.toString());
//                System.out.println("a + " + aggregation);
//                System.out.println("cccc " + criteria.get(0));
                AggregationResults<Car> results = mongoTemplate.aggregate(aggregation, "cars", Car.class);

                System.out.println("res: " + results.getMappedResults());
                return results.getMappedResults();
            }
            else
                return carRepo.findAll();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Criteria criteriaByRegex(String field, String word) {
        return new Criteria(field).regex((".*" + word + ".*"), "i");
    }

    public ResponseEntity<Car> getCarById(@PathVariable("id") String id) {
        Optional<Car> carData = carRepo.findById(id);

        if (carData.isPresent()) {
            return new ResponseEntity<>(carData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Car> updateCar(@PathVariable("id") String id, @RequestBody Car car) {
        Optional<Car> carData = carRepo.findById(id);

        if (carData.isPresent()) {
            Car c = carData.get();
            c.setName(car.getName());
            c.setFuel(car.getFuel());
            c.setKm_driven(car.getKm_driven());
            c.setOwner(car.getOwner());
            c.setSeller_type(car.getSeller_type());
            c.setSelling_price(car.getSelling_price());
            c.setTransmission(car.getTransmission());
            c.setYear(car.getYear());
            return new ResponseEntity<>(carRepo.save(c), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<HttpStatus> deleteCar(@PathVariable("id") String id) {
        try {
            carRepo.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<HttpStatus> deleteAllCars() {
        try {
            carRepo.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
