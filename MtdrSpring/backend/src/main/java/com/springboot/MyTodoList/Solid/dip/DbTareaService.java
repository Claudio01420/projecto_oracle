package com.springboot.MyTodoList.solid.dip;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.List;

// Simula una “fuente real” (DB). Podrías cambiar a otra sin tocar el controlador.
@Primary
@Service
public class DbTareaService implements TareaService {
    public List<String> tareas() {
        return List.of("Tarea DB 1", "Tarea DB 2");
    }
}
