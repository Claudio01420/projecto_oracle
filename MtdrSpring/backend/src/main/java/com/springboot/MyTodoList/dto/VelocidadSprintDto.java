package com.springboot.MyTodoList.dto;

/**
 * DTO para mostrar velocidad por sprint (gráfica de velocidad)
 */
public class VelocidadSprintDto {

    private Long sprintId;
    private Integer numeroSprint;
    private String tituloSprint;

    private long tareasCompletadas;
    private double horasCompletadas;
    private double puntosCompletados; // si usas story points

    // Para comparación
    private long tareasPlaneadas;
    private double horasPlaneadas;

    public VelocidadSprintDto() {}

    // Getters y Setters
    public Long getSprintId() { return sprintId; }
    public void setSprintId(Long sprintId) { this.sprintId = sprintId; }

    public Integer getNumeroSprint() { return numeroSprint; }
    public void setNumeroSprint(Integer numeroSprint) { this.numeroSprint = numeroSprint; }

    public String getTituloSprint() { return tituloSprint; }
    public void setTituloSprint(String tituloSprint) { this.tituloSprint = tituloSprint; }

    public long getTareasCompletadas() { return tareasCompletadas; }
    public void setTareasCompletadas(long tareasCompletadas) { this.tareasCompletadas = tareasCompletadas; }

    public double getHorasCompletadas() { return horasCompletadas; }
    public void setHorasCompletadas(double horasCompletadas) { this.horasCompletadas = horasCompletadas; }

    public double getPuntosCompletados() { return puntosCompletados; }
    public void setPuntosCompletados(double puntosCompletados) { this.puntosCompletados = puntosCompletados; }

    public long getTareasPlaneadas() { return tareasPlaneadas; }
    public void setTareasPlaneadas(long tareasPlaneadas) { this.tareasPlaneadas = tareasPlaneadas; }

    public double getHorasPlaneadas() { return horasPlaneadas; }
    public void setHorasPlaneadas(double horasPlaneadas) { this.horasPlaneadas = horasPlaneadas; }
}
