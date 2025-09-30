package com.springboot.MyTodoList.solid.isp;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TareaMemoryAdapter implements TareaReader, TareaWriter {
    private final Map<Long,String> store = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    public TareaMemoryAdapter() {
        create("Demo 1");
        create("Demo 2");
    }

    public List<String> list() { return new ArrayList<>(store.values()); }
    public String getById(long id) { return store.get(id); }
    public long create(String titulo) {
        long id = seq.incrementAndGet();
        store.put(id, titulo);
        return id;
    }
    public boolean delete(long id) { return store.remove(id) != null; }
}
