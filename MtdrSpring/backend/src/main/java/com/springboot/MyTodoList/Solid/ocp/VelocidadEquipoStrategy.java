package com.springboot.MyTodoList.solid.ocp;

import org.springframework.stereotype.Component;

@Component
public class VelocidadEquipoStrategy implements KpiStrategy {
    public String type() { return "velocidad"; }
    public int calculate(int storyPointsCompletados, int sprints) {
        return sprints == 0 ? 0 : storyPointsCompletados / sprints;
    }
}
