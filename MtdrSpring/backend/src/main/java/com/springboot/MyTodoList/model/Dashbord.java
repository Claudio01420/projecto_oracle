package com.springboot.MyTodoList.model;

import javax.persistence.*;

@Entity
@Table(schema = "TMDV", name = "DASHBORD")
public class Dashbord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DASH")
    private Long id;

    @Column(name = "BURNOUT", length = 4000)
    private String burnout;

    @Column(name = "SPRINT", length = 4000)
    private String sprint;

    @Column(name = "VERSION_DEMO", length = 4000)
    private String versionDemo;

    @Column(name = "RIESGOS", length = 4000)
    private String riesgos;

    @Column(name = "KPI_ID")
    private Long kpiId;  

    public Dashbord() { }

    public Dashbord(Long id, String burnout, String sprint, String versionDemo, String riesgos, Long kpiId) {
        this.id = id;
        this.burnout = burnout;
        this.sprint = sprint;
        this.versionDemo = versionDemo;
        this.riesgos = riesgos;
        this.kpiId = kpiId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBurnout() { return burnout; }
    public void setBurnout(String burnout) { this.burnout = burnout; }

    public String getSprint() { return sprint; }
    public void setSprint(String sprint) { this.sprint = sprint; }

    public String getVersionDemo() { return versionDemo; }
    public void setVersionDemo(String versionDemo) { this.versionDemo = versionDemo; }

    public String getRiesgos() { return riesgos; }
    public void setRiesgos(String riesgos) { this.riesgos = riesgos; }

    public Long getKpiId() { return kpiId; }
    public void setKpiId(Long kpiId) { this.kpiId = kpiId; }
}
