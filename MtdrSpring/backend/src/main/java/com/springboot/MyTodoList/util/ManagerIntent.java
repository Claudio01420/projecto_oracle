package com.springboot.MyTodoList.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Intent expandido para el ChatGPT Manager.
 * Soporta acciones para todos los módulos: Tareas, Proyectos, Equipos, Sprints, Usuarios, KPIs.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManagerIntent {

    /**
     * Módulos disponibles en el sistema.
     */
    public enum Module {
        GENERAL,    // Saludos, ayuda, desconocido
        TASK,       // Tareas
        PROJECT,    // Proyectos
        TEAM,       // Equipos
        SPRINT,     // Sprints
        USER,       // Usuarios
        KPI         // Métricas y reportes
    }

    /**
     * Acciones disponibles por módulo.
     */
    public enum Action {
        // GENERAL
        GREETING,
        HELP,
        ASK_CLARIFICATION,
        UNKNOWN,

        // TASK
        LIST_TASKS,
        CREATE_TASK,
        GET_TASK,
        UPDATE_TASK,
        MARK_DONE,
        MARK_PENDING,
        DELETE_TASK,

        // PROJECT
        LIST_PROJECTS,
        CREATE_PROJECT,
        GET_PROJECT,
        GET_PROJECT_TASKS,

        // TEAM
        LIST_TEAMS,
        CREATE_TEAM,
        GET_TEAM,
        GET_TEAM_MEMBERS,

        // SPRINT
        LIST_SPRINTS,
        CREATE_SPRINT,
        GET_SPRINT,
        GET_SPRINT_TASKS,

        // USER
        LIST_USERS,
        GET_USER,
        GET_MY_INFO,
        SET_MY_IDENTITY,      // Nuevo: para identificarse como un usuario
        GET_USER_TASKS,       // Nuevo: ver tareas de un usuario específico
        GET_USER_KPI,         // Nuevo: ver KPIs de un usuario específico

        // KPI
        GET_KPI_SUMMARY,
        GET_PROJECT_KPI,
        GET_SPRINT_KPI,
        GET_PRODUCTIVITY,
        GET_TASK_STATS,
        GET_BURNDOWN,           // Burndown chart del sprint
        GET_VELOCITY,           // Velocidad histórica del proyecto
        GET_TEAM_ICL,           // Índice de Carga Laboral (burnout del equipo)
        GET_TEAM_KPI,           // KPIs de todos los miembros del equipo
        GET_BURNOUT_USERS,      // Usuarios en riesgo de burnout
        GET_DASHBOARD,          // Dashboard completo del proyecto
        GET_SPRINT_HEALTH       // Estado de salud del sprint
    }

    // === Campos principales ===
    private Module module = Module.GENERAL;
    private Action action = Action.UNKNOWN;

    // === Campos de Tarea ===
    private Long taskId;
    private String taskTitle;
    private String description;
    private String newTitle;
    private String status;
    private String priority;
    private Double estimatedHours;

    // === Campos de Proyecto ===
    private Long projectId;
    private String projectName;

    // === Campos de Equipo ===
    private Long teamId;
    private String teamName;

    // === Campos de Sprint ===
    private Long sprintId;
    private String sprintTitle;
    private Integer sprintNumber;

    // === Campos de Usuario ===
    private Long userId;
    private String userName;

    // === Campos de Fechas ===
    @JsonProperty("dueDate")
    private String dueDateIso;

    @JsonProperty("startDate")
    private String startDateIso;

    @JsonProperty("endDate")
    private String endDateIso;

    // === Respuesta del bot ===
    private String botReply;

    // === Constructors ===
    public ManagerIntent() {}

    // === Factory methods ===
    public static ManagerIntent unknown(String message) {
        ManagerIntent intent = new ManagerIntent();
        intent.setModule(Module.GENERAL);
        intent.setAction(Action.UNKNOWN);
        intent.setBotReply(message);
        return intent;
    }

    public static ManagerIntent greeting(String message) {
        ManagerIntent intent = new ManagerIntent();
        intent.setModule(Module.GENERAL);
        intent.setAction(Action.GREETING);
        intent.setBotReply(message);
        return intent;
    }

    public static ManagerIntent help(String message) {
        ManagerIntent intent = new ManagerIntent();
        intent.setModule(Module.GENERAL);
        intent.setAction(Action.HELP);
        intent.setBotReply(message);
        return intent;
    }

    // === Getters y Setters ===

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNewTitle() {
        return newTitle;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Double getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Double estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public String getSprintTitle() {
        return sprintTitle;
    }

    public void setSprintTitle(String sprintTitle) {
        this.sprintTitle = sprintTitle;
    }

    public Integer getSprintNumber() {
        return sprintNumber;
    }

    public void setSprintNumber(Integer sprintNumber) {
        this.sprintNumber = sprintNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDueDateIso() {
        return dueDateIso;
    }

    public void setDueDateIso(String dueDateIso) {
        this.dueDateIso = dueDateIso;
    }

    public String getStartDateIso() {
        return startDateIso;
    }

    public void setStartDateIso(String startDateIso) {
        this.startDateIso = startDateIso;
    }

    public String getEndDateIso() {
        return endDateIso;
    }

    public void setEndDateIso(String endDateIso) {
        this.endDateIso = endDateIso;
    }

    public String getBotReply() {
        return botReply;
    }

    public void setBotReply(String botReply) {
        this.botReply = botReply;
    }

    @Override
    public String toString() {
        return "ManagerIntent{" +
                "module=" + module +
                ", action=" + action +
                ", taskId=" + taskId +
                ", projectId=" + projectId +
                ", teamId=" + teamId +
                ", sprintId=" + sprintId +
                ", botReply='" + botReply + '\'' +
                '}';
    }
}
