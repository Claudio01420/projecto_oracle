package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Kpi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KpiRepository extends JpaRepository<Kpi, Long> {}
