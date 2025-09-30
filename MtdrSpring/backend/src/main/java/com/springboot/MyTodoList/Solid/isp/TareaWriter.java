package com.springboot.MyTodoList.solid.isp;

public interface TareaWriter {
    long create(String titulo);
    boolean delete(long id);
}
