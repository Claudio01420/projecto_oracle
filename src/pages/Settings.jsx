import {
  Tabs, Tab, Box, Typography, Grid, Paper, TextField, Button, Switch,
  FormControlLabel, Avatar, Stack, Divider
} from "@mui/material";
import { useState } from "react";
import SectionCard from "../components/SectionCard";

export default function Settings() {
  const [tab, setTab] = useState(0);
  return (
    <>
      <Typography variant="h4" sx={{ color:"#111", fontWeight:900, mb:2 }}>Ajustes</Typography>
      <Tabs value={tab} onChange={(_,v)=>setTab(v)} sx={{ mb:2 }}>
        <Tab label="Perfil" /><Tab label="Preferencias" />
      </Tabs>

      {tab===0 && (
        <Grid container spacing={2}>
          <Grid item xs={12} md={4}>
            <SectionCard title="Perfil">
              <Stack spacing={2} alignItems="center">
                <Avatar src="/avatar.png" sx={{ width: 84, height: 84 }} />
                <TextField label="Nombre" fullWidth defaultValue="Pedro A. Rodríguez" />
                <TextField label="Email" fullWidth defaultValue="pedro@ejemplo.com" />
                <Button variant="contained" sx={{ bgcolor:"#E1261C" }}>Guardar</Button>
              </Stack>
            </SectionCard>
          </Grid>
          <Grid item xs={12} md={8}>
            <SectionCard title="Equipo / Usuarios" actions={<Button size="small" variant="outlined">Invitar miembro</Button>}>
              <Paper variant="outlined" sx={{ p:2 }}>
                <Stack divider={<Divider />} spacing={1}>
                  {["Claudio Cantú", "Paulo Ibarra", "Diego Roca", "Francisco Pérez"].map((n,i)=>(
                    <Box key={i} sx={{ display:"flex", alignItems:"center", gap:1 }}>
                      <Avatar sx={{ width: 28, height: 28 }}>{n.split(" ")[0][0]}</Avatar>
                      <Typography sx={{ color:"#111" }}>{n}</Typography>
                      <Box sx={{ ml:"auto", color:"#6b7280", fontSize:13 }}>Dev</Box>
                    </Box>
                  ))}
                </Stack>
              </Paper>
            </SectionCard>
          </Grid>
        </Grid>
      )}

      {tab===1 && (
        <SectionCard title="Preferencias">
          <FormControlLabel control={<Switch defaultChecked />} label="Notificaciones" />
          <FormControlLabel control={<Switch />} label="Modo compacto" />
        </SectionCard>
      )}
    </>
  );
}


