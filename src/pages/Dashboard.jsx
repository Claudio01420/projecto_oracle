import { Grid, Typography, Paper, List, ListItem, ListItemText } from "@mui/material";
// IMPORTA COMO DEFAULT (sin llaves):
import StatTile from "../components/StatTile";
import SectionCard from "../components/SectionCard";

export default function Dashboard() {
  return (
    <>
      <Typography variant="h4" sx={{ color:"#111", fontWeight: 900, mb:2 }}>Dashboard</Typography>

      <Grid container spacing={2} sx={{ mb: 2 }}>
        <Grid item xs={12} md={4}><StatTile title="% Tareas completadas" value="80%" /></Grid>
        <Grid item xs={12} md={4}><StatTile title="Productividad" value="+20%" subtitle="vs. meta" /></Grid>
        <Grid item xs={12} md={4}><StatTile title="Uso OCI" value="72%" /></Grid>
      </Grid>

      <Grid container spacing={2}>
        <Grid item xs={12} md={6}>
          <SectionCard title="Proyectos Activos">
            <List>
              {[
                { n:"Proyecto Alfa", estado:"En curso", avance:"70%" },
                { n:"Proyecto Bravo", estado:"Pendiente", avance:"0%" },
                { n:"Proyecto Chatfy", estado:"Finalizado", avance:"100%" },
              ].map((p,i)=>(
                <ListItem key={i} divider>
                  <ListItemText primary={p.n} secondary={`${p.estado} — Avance ${p.avance}`} />
                </ListItem>
              ))}
            </List>
          </SectionCard>
        </Grid>
        <Grid item xs={12} md={6}>
          <SectionCard title="Actividad Reciente">
            <Paper sx={{ p:2 }}>Tareas y eventos recientes…</Paper>
          </SectionCard>
        </Grid>
      </Grid>
    </>
  );
}


