package com.springboot.MyTodoList.solid.dip;

import org.springframework.stereotype.Service;
import java.util.List;

// Otra implementación posible (simula venir de otra API)
@Service
public class ApiTareaService implements TareaService {
    public List<String> tareas() {
        return List.of("Tarea API A", "Tarea API B");
    }
}
