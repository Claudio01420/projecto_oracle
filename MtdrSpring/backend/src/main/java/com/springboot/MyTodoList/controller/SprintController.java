package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.repository.SprintRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sprints")
public class SprintController {

    private final SprintRepository repo;
    public SprintController(SprintRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Sprint> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Sprint one(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @PostMapping
    public Sprint create(@RequestBody Sprint s) { return repo.save(s); }
}
