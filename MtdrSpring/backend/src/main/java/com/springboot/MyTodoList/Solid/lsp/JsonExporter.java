package com.springboot.MyTodoList.solid.lsp;

import org.springframework.stereotype.Component;

@Component
public class JsonExporter implements ReportExporter {
    public String export(String data) {
        return "{\"data\":\"" + data.replace("\"","\\\"") + "\"}";
    }
}
