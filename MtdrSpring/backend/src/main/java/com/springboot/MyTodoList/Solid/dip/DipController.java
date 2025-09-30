package com.springboot.MyTodoList.solid.dip;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/solid/dip")
public class DipController {
    private final TareaService tareaService; 

    public DipController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    
    @GetMapping("/tareas")
    public List<String> tareas() { return tareaService.tareas(); }
}
