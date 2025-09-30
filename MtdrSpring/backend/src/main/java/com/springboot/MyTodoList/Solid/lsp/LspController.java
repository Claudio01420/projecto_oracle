package com.springboot.MyTodoList.solid.lsp;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/solid/lsp")
public class LspController {
    private final List<ReportExporter> exporters;
    public LspController(List<ReportExporter> exporters) { this.exporters = exporters; }

    
    @GetMapping("/export")
    public String export(@RequestParam String fmt, @RequestParam String data) {
        return exporters.stream()
                .filter(e -> e.getClass().getSimpleName().toLowerCase().startsWith(fmt.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Formato no soportado"))
                .export(data);
    }
}
