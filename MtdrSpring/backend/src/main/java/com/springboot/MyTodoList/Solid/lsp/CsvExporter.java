package com.springboot.MyTodoList.solid.lsp;

import org.springframework.stereotype.Component;

@Component
public class CsvExporter implements ReportExporter {
    public String export(String data) {
        return "data\n" + data.replace("\n", " ");
    }
}
