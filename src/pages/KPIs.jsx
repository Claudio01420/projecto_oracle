import { Grid, Typography, Paper } from "@mui/material";
import StatTile from "../components/StatTile";
import SectionCard from "../components/SectionCard";
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, LineChart, Line, CartesianGrid, Tooltip } from "recharts";

const donePct = [
  { sprint: "S1", pct: 60 }, { sprint: "S2", pct: 65 }, { sprint: "S3", pct: 70 }, { sprint: "S4", pct: 75 }
];
const hours = [
  { s: "S1", reales: 12, plan: 10 }, { s: "S2", reales: 14, plan: 12 }, { s: "S3", reales: 13, plan: 12 }, { s: "S4", reales: 15, plan: 13 }
];

export default function KPIs() {
  return (
    <>
      <Typography variant="h4" sx={{ color:"#111", fontWeight: 900, mb:2 }}>KPIs de productividad y calidad</Typography>

      <Grid container spacing={2} sx={{ mb: 1 }}>
        <Grid item xs={12} md={3}><StatTile title="Task Completion" value="80%" /></Grid>
        <Grid item xs={12} md={3}><StatTile title="Tiempo prom. resolución" value="6.5 h" /></Grid>
        <Grid item xs={12} md={3}><StatTile title="Individual KPI Time" value="+18%" /></Grid>
        <Grid item xs={12} md={3}><StatTile title="Uso de OCI" value="76%" /></Grid>
      </Grid>

      <Grid container spacing={2}>
        <Grid item xs={12} md={6}>
          <SectionCard title="% tareas completadas por sprint">
            <div style={{ width:"100%", height:280 }}>
              <ResponsiveContainer>
                <BarChart data={donePct}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="sprint" /><YAxis /><Tooltip />
                  <Bar dataKey="pct" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </SectionCard>
        </Grid>
        <Grid item xs={12} md={6}>
          <SectionCard title="Horas reales vs planificadas">
            <div style={{ width:"100%", height:280 }}>
              <ResponsiveContainer>
                <LineChart data={hours}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="s" /><YAxis /><Tooltip />
                  <Line type="monotone" dataKey="reales" />
                  <Line type="monotone" dataKey="plan" />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </SectionCard>
        </Grid>
      </Grid>
    </>
  );
}



