import { Grid, Typography, Paper, List, ListItem, ListItemText, Switch, FormControlLabel, RadioGroup, Radio, FormControl, FormLabel, FormGroup, Checkbox } from "@mui/material";
import SectionCard from "../components/SectionCard";

export default function Notifications() {
  const items = [
    ["Nueva tarea asignada","Hoy 10:45 – Tareas"],
    ["Sprint Frontend completado","Ayer 16:20 – Sprints"],
    ["Actualización en Proyecto A","22 de abril, 10:15 – Proyectos"],
    ["Comentario en tarea","21 de abril – Tareas"],
    ["Nuevo miembro en equipo","20 de abril, 8:30 – Equipo"]
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
            </FormGroup>
            <FormControl sx={{ mt:2 }}>
              <FormLabel>Estado</FormLabel>
              <RadioGroup defaultValue="unread">
                <FormControlLabel value="unread" control={<Radio />} label="No leídas" />
                <FormControlLabel value="read" control={<Radio />} label="Leídas" />
              </RadioGroup>
            </FormControl>
          </SectionCard>
        </Grid>
        <Grid item xs={12} md={9}>
          <SectionCard title="Lista" actions={<FormControlLabel control={<Switch defaultChecked />} label="On" />}>
            <Paper variant="outlined" sx={{ p:0 }}>
              <List>
                {items.map((x,i)=>(
                  <ListItem key={i} divider
                    secondaryAction={<Typography sx={{ color:"#2563eb", cursor:"pointer" }}>Marcar como leído</Typography>}>
                    <ListItemText primaryTypographyProps={{ sx:{ color:"#111", fontWeight:700 } }}
                                  primary={x[0]} secondary={x[1]} />
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


