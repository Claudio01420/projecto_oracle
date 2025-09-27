package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Dashbord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashbordRepository extends JpaRepository<Dashbord, Long> {}
