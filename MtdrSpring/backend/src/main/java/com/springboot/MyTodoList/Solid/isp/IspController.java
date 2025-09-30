package com.springboot.MyTodoList.solid.isp;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/solid/isp")
public class IspController {
    private final TareaReader reader;
    private final TareaWriter writer;

    public IspController(TareaReader reader, TareaWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    
    @GetMapping("/list")
    public List<String> list() { return reader.list(); }

    
    @PostMapping("/create")
    public Map<String,Object> create(@RequestParam String titulo) {
        long id = writer.create(titulo);
        return Map.of("id", id, "titulo", titulo);
    }
}
