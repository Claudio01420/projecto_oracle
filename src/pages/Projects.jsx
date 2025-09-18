import { Grid, Typography, Table, TableBody, TableCell, TableHead, TableRow, TextField, InputAdornment, Paper } from "@mui/material";
import SearchRounded from "@mui/icons-material/SearchRounded";
import SectionCard from "../components/SectionCard";

export default function Projects() {
  return (
    <>
      <Typography variant="h4" sx={{ color:"#111", fontWeight: 900, mb: 2 }}>
        Proyectos
      </Typography>

      <Grid container spacing={2}>
        <Grid item xs={12} md={7}>
          <SectionCard
            title="Historial de proyectos"
            actions={
              <TextField size="small" placeholder="Buscar…" InputProps={{
                startAdornment: <InputAdornment position="start"><SearchRounded fontSize="small"/></InputAdornment>
              }} />
            }>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Nombre</TableCell>
                  <TableCell>Estado</TableCell>
                  <TableCell>Inicio</TableCell>
                  <TableCell>Fin</TableCell>
                  <TableCell>Avance</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {[
                  ["Proyecto Alfa","En curso","01/08/2025","","70%"],
                  ["Proyecto Bravo","Pendiente","15/08/2025","","0%"],
                  ["Proyecto Chatfy","Finalizado","01/06/2025","30/07/2025","100%"],
                ].map((r,i)=>(
                  <TableRow key={i} hover>
                    {r.map((c,j)=><TableCell key={j}>{c}</TableCell>)}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </SectionCard>
        </Grid>

        <Grid item xs={12} md={5}>
          <SectionCard title="Detalle: Proyecto Alfa">
            <Paper sx={{ p: 2, border:"1px solid #eef0f3" }}>
              <Typography sx={{ color:"#111", fontWeight:700, mb: .5 }}>Descripción</Typography>
              <Typography sx={{ color:"#6b7280", mb: 2 }}>Migración a OCI + OKE + ADB. Chatbot de Telegram integrado.</Typography>
              <Typography sx={{ color:"#111", fontWeight:700, mb: .5 }}>Integrantes</Typography>
              <Typography sx={{ color:"#6b7280" }}>Pedro, Claudio, Paulo, Diego, Francisco</Typography>
            </Paper>
          </SectionCard>
        </Grid>
      </Grid>
    </>
  );
}



