package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Dashbord;
import com.springboot.MyTodoList.repository.DashbordRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashbord")
public class DashbordController {

    private final DashbordRepository repo;

    public DashbordController(DashbordRepository repo) {
        this.repo = repo;
    }

    
    @GetMapping
    public List<Dashbord> all() {
        return repo.findAll();
    }

    
    @GetMapping("/{id}")
    public Dashbord one(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    
    @PostMapping
    public Dashbord create(@RequestBody Dashbord d) {
        return repo.save(d);
    }
}
