package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
   
    List<Proyecto> findByCreadorId(Long creadorId);
}
