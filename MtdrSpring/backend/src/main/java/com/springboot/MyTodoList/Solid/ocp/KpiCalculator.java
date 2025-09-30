package com.springboot.MyTodoList.solid.ocp;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class KpiCalculator {
    private final List<KpiStrategy> strategies;
    public KpiCalculator(List<KpiStrategy> strategies) { this.strategies = strategies; }

    public int calc(String type, int a, int b) {
        return strategies.stream()
                .filter(s -> s.type().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo KPI no soportado: " + type))
                .calculate(a, b);
    }
}
