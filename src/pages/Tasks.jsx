import { Grid, Typography, Paper, LinearProgress, Box } from "@mui/material";
import SectionCard from "../components/SectionCard";

const Column = ({ title, items=[] }) => (
  <Paper sx={{ p:2 }}>
    <Box sx={{ fontWeight: 700, mb: 1 }}>{title}</Box>
    {items.map((t,i)=>(
      <Paper key={i} sx={{ p:1.5, mb:1, border:"1px solid #eef0f3" }}>
        <Box sx={{ fontWeight:600 }}>{t.title}</Box>
        <Box sx={{ color:"#6b7280", fontSize:14 }}>{t.meta}</Box>
      </Paper>
    ))}
  </Paper>
);

export default function Tasks() {
  return (
    <>
      <Typography variant="h4" sx={{ color:"#111", fontWeight: 900, mb:2 }}>Tareas</Typography>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <SectionCard title="Progreso del Sprint General">
            <Box sx={{ display:"flex", alignItems:"center", gap:2 }}>
              <Box sx={{ minWidth: 140, color:"#111" }}>% de avance del sprint</Box>
              <Box sx={{ flex:1 }}>
                <LinearProgress variant="determinate" value={65} sx={{ height: 10, borderRadius: 999 }} />
              </Box>
              <Box sx={{ width: 60, textAlign:"right", color:"#111" }}>65%</Box>
            </Box>
          </SectionCard>
        </Grid>
        <Grid item xs={12}>
          <Grid container spacing={2}>
            <Grid item xs={12} md={4}><Column title="Por hacer" items={[{title:"Diseñar mockups", meta:"Prioridad alta"}, {title:"Presentación", meta:"Alta"}]} /></Grid>
            <Grid item xs={12} md={4}><Column title="En progreso" items={[{title:"Escribir documentación", meta:"En curso"}, {title:"Implementar API REST", meta:"Sprint 3"}]} /></Grid>
            <Grid item xs={12} md={4}><Column title="Hecho" items={[{title:"Integraciones básicas", meta:"Ok"}]} /></Grid>
          </Grid>
        </Grid>
      </Grid>
    </>
  );
}


