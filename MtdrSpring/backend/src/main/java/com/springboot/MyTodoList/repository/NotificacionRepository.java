package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {}
