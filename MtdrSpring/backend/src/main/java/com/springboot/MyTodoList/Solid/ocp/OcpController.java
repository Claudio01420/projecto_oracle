package com.springboot.MyTodoList.solid.ocp;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solid/ocp")
public class OcpController {
    private final KpiCalculator calculator;
    public OcpController(KpiCalculator calculator) { this.calculator = calculator; }

    @GetMapping("/kpi")
    public int kpi(@RequestParam String type, @RequestParam int a, @RequestParam int b) {
        return calculator.calc(type, a, b);
    }
}
