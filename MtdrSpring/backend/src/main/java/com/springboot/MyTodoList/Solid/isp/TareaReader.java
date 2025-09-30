package com.springboot.MyTodoList.solid.isp;

import java.util.List;

public interface TareaReader {
    List<String> list();
    String getById(long id);
}
