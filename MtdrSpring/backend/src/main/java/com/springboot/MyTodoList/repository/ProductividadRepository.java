package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Productividad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductividadRepository extends JpaRepository<Productividad, Long> {}
