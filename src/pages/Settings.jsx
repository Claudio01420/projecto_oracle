import { Tabs, Tab, Box, Typography, Grid, Paper, TextField, Button, Switch, FormControlLabel } from "@mui/material";
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
              <TextField label="Nombre" fullWidth sx={{ mb:2 }} />
              <TextField label="Email" fullWidth />
            </SectionCard>
          </Grid>
          <Grid item xs={12} md={8}>
            <SectionCard title="Equipo / Usuarios" actions={<Button variant="contained" sx={{ bgcolor:"#E1261C" }}>Invitar miembro</Button>}>
              <Paper variant="outlined" sx={{ p:2 }}>Lista de usuarios (placeholder)…</Paper>
            </SectionCard>
          </Grid>
        </Grid>
      )}

      {tab===1 && (
        <SectionCard title="Preferencias">
          <FormControlLabel control={<Switch defaultChecked />} label="Notificaciones" />
        </SectionCard>
      )}
    </>
  );
}


