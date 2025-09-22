import { useEffect, useState } from "react";
import {Grid,Typography,Table,TableBody,TableCell,TableHead,TableRow,LinearProgress,Chip,Button,Dialog,DialogTitle,DialogContent,DialogActions,TextField} 
from "@mui/material";
import SectionCard from "../components/SectionCard";

export default function Projects() {
  const [projects, setProjects] = useState([]);
  const [open, setOpen] = useState(false);
  const [selectedProject, setSelectedProject] = useState(null); // ✅ estado para proyecto seleccionado
  const [newProject, setNewProject] = useState({
    nombre: "",
    descripcion: "",
    estado: "Pendiente",
    fechaInicio: "",
    fechaFin: ""
  });

  useEffect(() => {
    setProjects([
      {
        PROYECTO_ID: 1,
        NOMBRE_PROYECTO: "Proyecto Alfa",
        DESCRIPCION: "Descripción alfa",
        ESTADO: "Finalizado",
        FECHA_INICIO: "2025-08-01",
        FECHA_FIN: "2025-12-31"
      },
      {
        PROYECTO_ID: 2,
        NOMBRE_PROYECTO: "Proyecto Beta",
        DESCRIPCION: "Descripción beta",
        ESTADO: "En curso",
        FECHA_INICIO: "2025-09-01",
        FECHA_FIN: "2025-10-15"
      }
    ]);
  }, []);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const handleChange = (e) => {
    setNewProject({ ...newProject, [e.target.name]: e.target.value });
  };

  const handleCreate = () => {
    const nuevo = {
      PROYECTO_ID: projects.length + 1,
      NOMBRE_PROYECTO: newProject.nombre,
      DESCRIPCION: newProject.descripcion,
      ESTADO: newProject.estado,
      FECHA_INICIO: newProject.fechaInicio,
      FECHA_FIN: newProject.fechaFin
    };
    setProjects([...projects, nuevo]);
    setOpen(false);
  };

  return (
    <>
      <Grid container alignItems="center" justifyContent="space-between" sx={{ mb: 2 }}>
        <Typography variant="h4" sx={{ color: "black", fontWeight: 900 }}>
          Proyectos
        </Typography>
        <Button variant="contained" color="primary" onClick={handleOpen}>
          + Crear Proyecto
        </Button>
      </Grid>

      <Grid container spacing={2}>
        <Grid item xs={12} md={7}>
          <SectionCard title="Historial de Proyectos">
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell sx={{ color: "black" }}>Nombre</TableCell>
                  <TableCell sx={{ color: "black" }}>Estado</TableCell>
                  <TableCell sx={{ color: "black" }}>Inicio</TableCell>
                  <TableCell sx={{ color: "black" }}>Fin</TableCell>
                  <TableCell sx={{ color: "black" }}>Avance</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {projects.map((p) => (
                  <TableRow
                    key={p.PROYECTO_ID}
                    hover
                    selected={selectedProject?.PROYECTO_ID === p.PROYECTO_ID} // ✅ fila seleccionada
                    sx={{
                      cursor: "pointer",
                      backgroundColor:
                        selectedProject?.PROYECTO_ID === p.PROYECTO_ID
                          ? "#e0e0e0"
                          : "inherit",
                      "&:hover": {
                        backgroundColor:
                          selectedProject?.PROYECTO_ID === p.PROYECTO_ID
                            ? "#d5d5d5"
                            : "#f5f5f5"
                      }
                    }}
                    onClick={() => setSelectedProject(p)} // ✅ seleccionar proyecto
                  >
                    <TableCell sx={{ color: "black" }}>{p.NOMBRE_PROYECTO}</TableCell>
                    <TableCell>
                      <Chip
                        label={p.ESTADO}
                        color={
                          p.ESTADO === "Finalizado"
                            ? "success"
                            : p.ESTADO === "En curso"
                            ? "primary"
                            : "warning"
                        }
                        size="small"
                      />
                    </TableCell>
                    <TableCell sx={{ color: "black" }}>
                      {p.FECHA_INICIO ? new Date(p.FECHA_INICIO).toLocaleDateString() : "-"}
                    </TableCell>
                    <TableCell sx={{ color: "black" }}>
                      {p.FECHA_FIN ? new Date(p.FECHA_FIN).toLocaleDateString() : "-"}
                    </TableCell>
                    <TableCell>
                      <LinearProgress
                        variant="determinate"
                        value={p.ESTADO === "Finalizado" ? 100 : p.ESTADO === "Pendiente" ? 0 : 50}
                      />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </SectionCard>
        </Grid>

        <Grid item xs={12} md={5}>
          <SectionCard title="Detalle del Proyecto">
            {selectedProject ? (
              <>
                <Typography variant="h6" sx={{ color: "black" }}>
                  {selectedProject.NOMBRE_PROYECTO}
                </Typography>
                <Typography variant="body2" sx={{ color: "black", mb: 2 }}>
                  {selectedProject.DESCRIPCION}
                </Typography>
                <Typography variant="body2" sx={{ color: "black" }}>
                  Estado: {selectedProject.ESTADO}
                </Typography>
                <Typography variant="body2" sx={{ color: "black" }}>
                  Inicio: {selectedProject.FECHA_INICIO}
                </Typography>
                <Typography variant="body2" sx={{ color: "black" }}>
                  Fin: {selectedProject.FECHA_FIN}
                </Typography>
              </>
            ) : (
              <Typography variant="body2" sx={{ color: "black" }}>
                Selecciona un proyecto de la tabla
              </Typography>
            )}
          </SectionCard>
        </Grid>
      </Grid>

      {/* Dialog para crear proyecto */}
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle sx={{ color: "black" }}>Crear Proyecto</DialogTitle>
        <DialogContent
          sx={{
            display: "flex",
            flexDirection: "column",
            gap: 2,
            mt: 0,
            color: "black"
          }}
        >
          <TextField
            label="Nombre"
            name="nombre"
            value={newProject.nombre}
            onChange={handleChange}
            fullWidth
            InputLabelProps={{ style: { color: "black" } }}
            InputProps={{ style: { color: "black" } }}
          />
          <TextField
            label="Descripción"
            name="descripcion"
            value={newProject.descripcion}
            onChange={handleChange}
            fullWidth
            multiline
            InputLabelProps={{ style: { color: "black" } }}
            InputProps={{ style: { color: "black" } }}
          />
          <TextField
            label="Fecha Inicio"
            name="fechaInicio"
            type="date"
            InputLabelProps={{ shrink: true, style: { color: "black" } }}
            InputProps={{ style: { color: "black" } }}
            value={newProject.fechaInicio}
            onChange={handleChange}
          />
          <TextField
            label="Fecha Fin"
            name="fechaFin"
            type="date"
            InputLabelProps={{ shrink: true, style: { color: "black" } }}
            InputProps={{ style: { color: "black" } }}
            value={newProject.fechaFin}
            onChange={handleChange}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} sx={{ color: "black" }}>
            Cancelar
          </Button>
          <Button onClick={handleCreate} variant="contained">
            Guardar
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
}
