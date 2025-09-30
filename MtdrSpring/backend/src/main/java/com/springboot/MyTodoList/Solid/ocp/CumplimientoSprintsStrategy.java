package com.springboot.MyTodoList.solid.ocp;

import org.springframework.stereotype.Component;

@Component
public class CumplimientoSprintsStrategy implements KpiStrategy {
    public String type() { return "cumplimiento"; }
    public int calculate(int sprintsCumplidos, int sprintsTotales) {
        return sprintsTotales == 0 ? 0 : (100 * sprintsCumplidos) / sprintsTotales;
    }
}
