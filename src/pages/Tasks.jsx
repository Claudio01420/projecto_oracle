import { Grid, Typography, Paper, LinearProgress, Box } from "@mui/material";
import SectionCard from "../components/SectionCard";

const Column = ({ title, items=[] }) => (
  <Paper sx={{ p:2, border:"1px solid #eef0f3", bgcolor:"#fff" }}>
    <Box sx={{ fontWeight: 800, mb: 1, color:"#111" }}>{title}</Box>
    {items.map((t,i)=>(
      <Paper key={i} sx={{ p:1.5, mb:1, border:"1px solid #eef0f3", bgcolor:"#fafbfc" }}>
        <Box sx={{ fontWeight:700, color:"#111" }}>{t.title}</Box>
        <Box sx={{ color:"#6b7280", fontSize:13 }}>{t.meta}</Box>
      </Paper>
    ))}
  </Paper>
);

export default function Tasks() {
  return (
    <>
      <Typography variant="h4" sx={{ color:"#111", fontWeight: 900, mb:2 }}>Tareas</Typography>

      <SectionCard title="Progreso del sprint">
        <Box sx={{ display:"flex", alignItems:"center", gap:2 }}>
          <Box sx={{ minWidth: 160, color:"#111" }}>Avance general</Box>
          <Box sx={{ flex:1 }}>
            <LinearProgress variant="determinate" value={65} sx={{ height: 10, borderRadius: 999 }} />
          </Box>
          <Box sx={{ width: 60, textAlign:"right", color:"#111" }}>65%</Box>
        </Box>
      </SectionCard>

      <Grid container spacing={2} sx={{ mt: .5 }}>
        <Grid item xs={12} md={4}><Column title="Por hacer" items={[{title:"Diseñar mockups", meta:"Alta"}, {title:"Presentación", meta:"Alta"}]} /></Grid>
        <Grid item xs={12} md={4}><Column title="En progreso" items={[{title:"Doc técnica", meta:"En curso"}, {title:"API REST", meta:"Sprint 3"}]} /></Grid>
        <Grid item xs={12} md={4}><Column title="Hecho" items={[{title:"Integraciones básicas", meta:"OK"}]} /></Grid>
      </Grid>
    </>
  );
}



