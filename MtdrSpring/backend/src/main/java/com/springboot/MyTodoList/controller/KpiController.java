package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Kpi;
import com.springboot.MyTodoList.repository.KpiRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kpis")
public class KpiController {

    private final KpiRepository repo;
    public KpiController(KpiRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Kpi> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Kpi one(@PathVariable Long id) { return repo.findById(id).orElse(null); }

    @PostMapping
    public Kpi create(@RequestBody Kpi k) { return repo.save(k); }
}
