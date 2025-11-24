package com.springboot.MyTodoList.dto;

import java.time.LocalDate;

/**
 * DTO para un punto en la gráfica de burndown
 */
public class BurndownPointDto {

    private LocalDate fecha;
    private long tareasRestantes;
    private long tareasCompletadas;
    private double horasRestantes;
    private double horasCompletadas;
    private double idealRestante; // línea ideal de burndown

    public BurndownPointDto() {}

    public BurndownPointDto(LocalDate fecha, long tareasRestantes, long tareasCompletadas,
                            double horasRestantes, double horasCompletadas, double idealRestante) {
        this.fecha = fecha;
        this.tareasRestantes = tareasRestantes;
        this.tareasCompletadas = tareasCompletadas;
        this.horasRestantes = horasRestantes;
        this.horasCompletadas = horasCompletadas;
        this.idealRestante = idealRestante;
    }

    // Getters y Setters
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public long getTareasRestantes() { return tareasRestantes; }
    public void setTareasRestantes(long tareasRestantes) { this.tareasRestantes = tareasRestantes; }

    public long getTareasCompletadas() { return tareasCompletadas; }
    public void setTareasCompletadas(long tareasCompletadas) { this.tareasCompletadas = tareasCompletadas; }

    public double getHorasRestantes() { return horasRestantes; }
    public void setHorasRestantes(double horasRestantes) { this.horasRestantes = horasRestantes; }

    public double getHorasCompletadas() { return horasCompletadas; }
    public void setHorasCompletadas(double horasCompletadas) { this.horasCompletadas = horasCompletadas; }

    public double getIdealRestante() { return idealRestante; }
    public void setIdealRestante(double idealRestante) { this.idealRestante = idealRestante; }
}
