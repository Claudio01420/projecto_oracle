package com.springboot.MyTodoList.dto;

/**
 * DTO con los KPIs de un miembro del equipo
 */
public class KpiMiembroDto {

    private String odaId;
    private String nombre;
    private String email;

    // Métricas de tareas
    private long tareasAsignadas;
    private long tareasCompletadas;
    private long tareasPendientes;
    private double porcentajeCompletado;

    // Métricas de horas
    private double horasEstimadas;
    private double horasReales;
    private double eficiencia;

    // Métricas de productividad
    private double tareasPromedioSprint;
    private double horasPromedioPorTarea;

    public KpiMiembroDto() {}

    // Getters y Setters
    public String getOdaId() { return odaId; }
    public void setOdaId(String odaId) { this.odaId = odaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public long getTareasAsignadas() { return tareasAsignadas; }
    public void setTareasAsignadas(long tareasAsignadas) { this.tareasAsignadas = tareasAsignadas; }

    public long getTareasCompletadas() { return tareasCompletadas; }
    public void setTareasCompletadas(long tareasCompletadas) { this.tareasCompletadas = tareasCompletadas; }

    public long getTareasPendientes() { return tareasPendientes; }
    public void setTareasPendientes(long tareasPendientes) { this.tareasPendientes = tareasPendientes; }

    public double getPorcentajeCompletado() { return porcentajeCompletado; }
    public void setPorcentajeCompletado(double porcentajeCompletado) { this.porcentajeCompletado = porcentajeCompletado; }

    public double getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(double horasEstimadas) { this.horasEstimadas = horasEstimadas; }

    public double getHorasReales() { return horasReales; }
    public void setHorasReales(double horasReales) { this.horasReales = horasReales; }

    public double getEficiencia() { return eficiencia; }
    public void setEficiencia(double eficiencia) { this.eficiencia = eficiencia; }

    public double getTareasPromedioSprint() { return tareasPromedioSprint; }
    public void setTareasPromedioSprint(double tareasPromedioSprint) { this.tareasPromedioSprint = tareasPromedioSprint; }

    public double getHorasPromedioPorTarea() { return horasPromedioPorTarea; }
    public void setHorasPromedioPorTarea(double horasPromedioPorTarea) { this.horasPromedioPorTarea = horasPromedioPorTarea; }
}
