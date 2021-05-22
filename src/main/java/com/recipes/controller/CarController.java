package com.recipes.controller;

import com.recipes.entity.Car;
import com.recipes.entity.Params;
import com.recipes.repo.CarRepo;
import com.recipes.service.CarService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class CarController {
    CarService carService;
    CarRepo carRepo;

    @GetMapping("/")
    public String index(Model model){
        return "redirect:index";
    }

    @GetMapping("/index")
    public String getAll(Model model){
        Params params = new Params();
        model.addAttribute("params", params);
        model.addAttribute("cars", carRepo.findAll());
        System.out.println(carRepo.findAll());
        return "index";
    }

//    @GetMapping("/sort")
//    public String sort(Model model){
//        carService.sort();
//        return "redirect:index";
//    }

    @GetMapping("/create")
    public String createCar(Model model){
        System.out.println("create");
        model.addAttribute("car", new Car());
        return "create";
    }

    @GetMapping("/charts")
    public String getCharts(Model model){
        System.out.println("getCharts");
        return "charts";
    }

    @PostMapping("/add")
    public String addCar(@ModelAttribute Car car){
        System.out.println("add");
        System.out.println(car.getName());
        carRepo.save(car);
        return "redirect:index";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") String id, Model model) {
        Car car = carRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid car Id:" + id));
        carRepo.delete(car);
        return "redirect:/index";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, Model model) {
        Car car = carRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("car", car);
        return "edit";
    }

    @PostMapping("/save/{id}")
    public String updateUser(@PathVariable("id") String id, Car car,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            car.setId(id);
            return "update-user";
        }
        System.out.println("save");
        carRepo.save(car);
        return "redirect:/index";
    }

    @GetMapping("/search")
    public String search(Model model, @Param("params") Params params){
        model.addAttribute("cars", carService.search(params));
        return "index";
    }


    @GetMapping("/deleteAll")
    public String deleteAll(Model model){
        carRepo.deleteAll();
        model.addAttribute("cars", carRepo.findAll());
        return "index";
    }

}
