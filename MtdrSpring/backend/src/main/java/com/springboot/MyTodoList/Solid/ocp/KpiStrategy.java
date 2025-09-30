package com.springboot.MyTodoList.solid.ocp;

public interface KpiStrategy {
    String type();
    int calculate(int a, int b);
}
