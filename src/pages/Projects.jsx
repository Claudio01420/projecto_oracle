import { Grid, Typography, Table, TableBody, TableCell, TableHead, TableRow } from "@mui/material";
import SectionCard from "../components/SectionCard";

export default function Projects() {
  return (
    <>
      <Typography variant="h4" sx={{ color:"#111", fontWeight: 900, mb:2 }}>Proyectos</Typography>
      <Grid container spacing={2}>
        <Grid item xs={12} md={6}>
          <SectionCard title="Historial de Proyectos">
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Nombre</TableCell><TableCell>Estado</TableCell><TableCell>Inicio</TableCell><TableCell>Fin</TableCell><TableCell>Avance</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {[
                  ["Proyecto Alfa","En curso","01/08/2025","","70%"],
                  ["Proyecto Bravo","Pendiente","15/08/2025","","0%"],
                  ["Proyecto Chatfy","Finalizado","01/06/2025","30/07/2025","100%"],
                ].map((r,i)=>(
                  <TableRow key={i}>{r.map((c,j)=><TableCell key={j}>{c}</TableCell>)}</TableRow>
                ))}
              </TableBody>
            </Table>
          </SectionCard>
        </Grid>
        <Grid item xs={12} md={6}>
          <SectionCard title="Detalle del Proyecto A">
            <div>Descripción, responsables, integrantes y mini-kanban (placeholder)…</div>
          </SectionCard>
        </Grid>
      </Grid>
    </>
  );
}


