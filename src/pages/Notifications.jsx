import {
  Grid, Typography, Paper, List, ListItem, ListItemText, Switch, FormControlLabel,
  RadioGroup, Radio, FormControl, FormLabel, FormGroup, Checkbox, Chip, Stack
} from "@mui/material";
import SectionCard from "../components/SectionCard";

export default function Notifications() {
  const data = [
    { t:"Nueva tarea asignada", d:"Hoy 10:45 – Tareas", type:"Tareas" },
    { t:"Sprint Frontend completado", d:"Ayer 16:20 – Sprints", type:"Sprints" },
    { t:"Actualización en Proyecto A", d:"22 de abril, 10:15 – Proyectos", type:"Proyectos" },
    { t:"Comentario en tarea", d:"21 de abril – Tareas", type:"Tareas" },
    { t:"Nuevo miembro en equipo", d:"20 de abril, 8:30 – Equipo", type:"Equipo" }
  ];

  return (
    <>
      <Typography variant="h4" sx={{ color:"#111", fontWeight: 900, mb:2 }}>Notificaciones</Typography>
      <Grid container spacing={2}>
        <Grid item xs={12} md={3}>
          <SectionCard title="Filtros">
            <FormGroup>
              <FormLabel>Tipo</FormLabel>
              <FormControlLabel control={<Checkbox defaultChecked />} label="Proyectos" />
              <FormControlLabel control={<Checkbox defaultChecked />} label="Tareas" />
              <FormControlLabel control={<Checkbox defaultChecked />} label="Sprints" />
              <FormControlLabel control={<Checkbox defaultChecked />} label="Equipo" />
            </FormGroup>
            <FormControl sx={{ mt:2 }}>
              <FormLabel>Estado</FormLabel>
              <RadioGroup defaultValue="unread">
                <FormControlLabel value="unread" control={<Radio />} label="No leídas" />
                <FormControlLabel value="read" control={<Radio />} label="Leídas" />
              </RadioGroup>
            </FormControl>
            <FormControlLabel sx={{ mt:1 }} control={<Switch defaultChecked />} label="Activar notificaciones" />
          </SectionCard>
        </Grid>

        <Grid item xs={12} md={9}>
          <SectionCard title="Bandeja">
            <Paper variant="outlined" sx={{ p:0 }}>
              <List>
                {data.map((x,i)=>(
                  <ListItem key={i} divider
                    secondaryAction={<Typography sx={{ color:"#2563eb", cursor:"pointer" }}>Marcar como leído</Typography>}>
                    <ListItemText
                      primaryTypographyProps={{ sx:{ color:"#111", fontWeight:700, display:"flex", alignItems:"center", gap:1 } }}
                      primary={<><span>{x.t}</span> <Chip size="small" label={x.type} /></>}
                      secondary={x.d}
                    />
                  </ListItem>
                ))}
              </List>
            </Paper>
          </SectionCard>
        </Grid>
      </Grid>
    </>
  );
}



