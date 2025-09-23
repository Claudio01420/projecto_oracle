import { useState } from "react";
import {
  Box,
  Grid,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  LinearProgress,
  Chip,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Snackbar,
  Alert,
  Select,
  MenuItem,
  InputLabel,
  FormControl,
  Paper,
  TableContainer,
  TablePagination,
  TableSortLabel,
  Stack,
} from "@mui/material";
import SectionCard from "../components/SectionCard";

/* ---------- Helpers UI ---------- */
function ProgressWithLabel({ value }) {
  return (
    <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
      <Box sx={{ flex: 1 }}>
        <LinearProgress variant="determinate" value={value} />
      </Box>
      <Typography variant="caption" sx={{ color: "black", minWidth: 42, textAlign: "right" }}>
        {`${Math.round(value)}%`}
      </Typography>
    </Box>
  );
}

function EstadoChip({ estado }) {
  const map = {
    "En curso": { color: "primary" },
    "Finalizado": { color: "success" },
    "Pendiente": { color: "warning" },
  };
  return <Chip label={estado} {...map[estado]} variant="outlined" size="small" />;
}

function fmtDate(iso) {
  try {
    return new Date(iso).toLocaleDateString();
  } catch {
    return iso ?? "";
  }
}

/* ---------- Ordenamiento ---------- */
function sortBy(arr, key, dir) {
  const sorted = [...arr].sort((a, b) => {
    const va = a[key];
    const vb = b[key];
    if (typeof va === "number" && typeof vb === "number") return va - vb;
    return String(va ?? "").localeCompare(String(vb ?? ""));
  });
  return dir === "asc" ? sorted : sorted.reverse();
}

export default function Projects() {
  const [projects, setProjects] = useState([
    {
      PROYECTO_ID: 1,
      NOMBRE: "Proyecto A",
      DESCRIPCION: "Descripción del Proyecto A",
      FECHA_INICIO: "2025-01-01",
      FECHA_FIN: "2025-06-01",
      ESTADO: "Finalizado",
      AVANCE: 100,
    },
    {
      PROYECTO_ID: 2,
      NOMBRE: "Proyecto B",
      DESCRIPCION: "Descripción del Proyecto B",
      FECHA_INICIO: "2025-02-01",
      FECHA_FIN: "2025-08-01",
      ESTADO: "En curso",
      AVANCE: 70,
    },
  ]);

  const [selectedProject, setSelectedProject] = useState(null);
  const [selectedId, setSelectedId] = useState(null);

  const [open, setOpen] = useState(false);
  const [newProject, setNewProject] = useState({
    nombre: "",
    descripcion: "",
    fechaInicio: "",
    fechaFin: "",
  });

  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("Todos");
  const [snackbar, setSnackbar] = useState({ open: false, message: "", type: "success" });

  // Confirmación de borrado
  const [confirmDelete, setConfirmDelete] = useState({ open: false, id: null });

  // Ordenamiento + paginación
  const [orderBy, setOrderBy] = useState("NOMBRE");
  const [order, setOrder] = useState("asc");
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);

  const handleRowClick = (project) => {
    setSelectedProject(project);
    setSelectedId(project.PROYECTO_ID);
  };

  const handleClickOpen = () => setOpen(true);
  const handleClose = () => {
    setOpen(false);
    setNewProject({ nombre: "", descripcion: "", fechaInicio: "", fechaFin: "" });
  };

  const handleCreate = () => {
    if (!newProject.nombre.trim()) {
      setSnackbar({ open: true, message: "El nombre es obligatorio", type: "warning" });
      return;
    }
    const newProj = {
      PROYECTO_ID: projects.length ? Math.max(...projects.map((p) => p.PROYECTO_ID)) + 1 : 1,
      NOMBRE: newProject.nombre,
      DESCRIPCION: newProject.descripcion,
      FECHA_INICIO: newProject.fechaInicio,
      FECHA_FIN: newProject.fechaFin,
      ESTADO: "Pendiente",
      AVANCE: 0,
    };
    setProjects((prev) => [...prev, newProj]);
    setNewProject({ nombre: "", descripcion: "", fechaInicio: "", fechaFin: "" });
    setOpen(false);
    setSnackbar({ open: true, message: "Proyecto creado con éxito", type: "success" });
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setNewProject((prev) => ({ ...prev, [name]: value }));
  };

  const filteredProjects = projects.filter((p) => {
    const matchSearch = p.NOMBRE.toLowerCase().includes(search.toLowerCase());
    const matchFilter = filter === "Todos" || p.ESTADO === filter;
    return matchSearch && matchFilter;
  });

  const sortedProjects = sortBy(filteredProjects, orderBy, order);
  const pageData = sortedProjects.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);

  const handleSort = (key) => {
    if (orderBy === key) setOrder((prev) => (prev === "asc" ? "desc" : "asc"));
    else {
      setOrderBy(key);
      setOrder("asc");
    }
  };

  // estilo reutilizable para bordes negros
  const textFieldStyle = {
    "& .MuiOutlinedInput-root": {
      "& fieldset": {
        borderColor: "black",
      },
      "&:hover fieldset": {
        borderColor: "black",
      },
      "&.Mui-focused fieldset": {
        borderColor: "black",
      },
    },
  };

  return (
    <Grid container spacing={2}>
      {/* Lista de proyectos */}
      <Grid item xs={8}>
        <SectionCard title="Proyectos">
          {/* Toolbar superior: búsqueda + filtro + chips rápidos + botón */}
          <Grid container spacing={2} sx={{ mb: 2 }}>
            <Grid item xs={6}>
              <TextField
                label="Buscar proyecto"
                variant="outlined"
                size="small"
                fullWidth
                value={search}
                onChange={(e) => {
                  setSearch(e.target.value);
                  setPage(0);
                }}
                InputLabelProps={{ style: { color: "black" } }}
                InputProps={{ style: { color: "black" } }}
                sx={textFieldStyle}
              />
            </Grid>
            <Grid item xs={4}>
              <FormControl fullWidth size="small">
                <InputLabel sx={{ color: "black" }}>Filtrar</InputLabel>
                <Select
                  value={filter}
                  onChange={(e) => {
                    setFilter(e.target.value);
                    setPage(0);
                  }}
                  sx={{ color: "black" }}
                  label="Filtrar"
                >
                  <MenuItem value="Todos">Todos</MenuItem>
                  <MenuItem value="Pendiente">Pendiente</MenuItem>
                  <MenuItem value="En curso">En curso</MenuItem>
                  <MenuItem value="Finalizado">Finalizado</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={2}>
              <Button variant="contained" fullWidth onClick={handleClickOpen}>
                Nuevo
              </Button>
            </Grid>
          </Grid>

          {/* Chips rápidos y contador */}
          <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ mb: 1 }}>
            <Typography variant="subtitle2" sx={{ color: "black" }}>
              {filteredProjects.length} resultado(s) · {projects.length} total(es)
            </Typography>
            <Stack direction="row" spacing={1}>
              {["Todos", "Pendiente", "En curso", "Finalizado"].map((s) => (
                <Chip
                  key={s}
                  label={s}
                  variant={filter === s ? "filled" : "outlined"}
                  color={
                    filter === s
                      ? s === "Finalizado"
                        ? "success"
                        : s === "En curso"
                        ? "primary"
                        : "warning"
                      : "default"
                  }
                  onClick={() => {
                    setFilter(s);
                    setPage(0);
                  }}
                  sx={{ fontWeight: filter === s ? 700 : 400 }}
                />
              ))}
            </Stack>
          </Stack>

          {/* Tabla con encabezado pegajoso, zebra rows, selección roja */}
          {filteredProjects.length === 0 ? (
            <Typography sx={{ color: "black", py: 4, textAlign: "center" }}>
              No hay proyectos que coincidan con la búsqueda o el filtro.
            </Typography>
          ) : (
            <>
              <TableContainer component={Paper} sx={{ maxHeight: 420 }}>
                <Table stickyHeader size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell sx={{ color: "white", fontWeight: 700 }}>ID</TableCell>
                      <TableCell sx={{ color: "white", fontWeight: 700 }} sortDirection={orderBy === "NOMBRE" ? order : false}>
                        <TableSortLabel
                          active={orderBy === "black"}
                          direction={orderBy === "NOMBRE" ? order : "asc"}
                          onClick={() => handleSort("NOMBRE")}
                        >
                          Nombre
                        </TableSortLabel>
                      </TableCell>
                      <TableCell sx={{ color: "white", fontWeight: 700 }} sortDirection={orderBy === "ESTADO" ? order : false}>
                        <TableSortLabel
                          active={orderBy === "ESTADO"}
                          direction={orderBy === "ESTADO" ? order : "asc"}
                          onClick={() => handleSort("ESTADO")}
                        >
                          Estado
                        </TableSortLabel>
                      </TableCell>
                      <TableCell sx={{ color: "white", fontWeight: 700 }} sortDirection={orderBy === "AVANCE" ? order : false}>
                        <TableSortLabel
                          active={orderBy === "AVANCE"}
                          direction={orderBy === "AVANCE" ? order : "asc"}
                          onClick={() => handleSort("AVANCE")}
                        >
                          Avance
                        </TableSortLabel>
                      </TableCell>
                      <TableCell sx={{ color: "white", fontWeight: 700 }}>Acciones</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {pageData.map((project) => {
                      const isSelected = selectedId === project.PROYECTO_ID;
                      return (
                        <TableRow
                          key={project.PROYECTO_ID}
                          hover
                          onClick={() => handleRowClick(project)}
                          selected={isSelected}
                          sx={{
                            cursor: "pointer",
                            "&:nth-of-type(odd)": { backgroundColor: "rgba(0,0,0,0.02)" },
                            ...(isSelected && {
                              backgroundColor: "rgba(255,0,0,0.12)",
                              "&:hover": { backgroundColor: "rgba(255,0,0,0.18)" },
                            }),
                          }}
                        >
                          <TableCell sx={{ color: "black" }}>{project.PROYECTO_ID}</TableCell>
                          <TableCell sx={{ color: "black" }}>{project.NOMBRE}</TableCell>
                          <TableCell>
                            <EstadoChip estado={project.ESTADO} />
                          </TableCell>
                          <TableCell>
                            <ProgressWithLabel value={project.AVANCE} />
                          </TableCell>
                          <TableCell>
                            <Stack direction="row" spacing={1}>
                              <Button
                                variant="outlined"
                                color="error"
                                size="small"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  setConfirmDelete({ open: true, id: project.PROYECTO_ID });
                                }}
                              >
                                Borrar
                              </Button>
                            </Stack>
                          </TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </TableContainer>

              <TablePagination
                component="div"
                count={filteredProjects.length}
                page={page}
                onPageChange={(_, p) => setPage(p)}
                rowsPerPage={rowsPerPage}
                onRowsPerPageChange={(e) => {
                  setRowsPerPage(parseInt(e.target.value, 10));
                  setPage(0);
                }}
                rowsPerPageOptions={[5, 10, 25]}
              />
            </>
          )}
        </SectionCard>
      </Grid>

      {/* Detalle del proyecto */}
      <Grid item xs={4}>
        <SectionCard title="Detalle del Proyecto">
          {selectedProject ? (
            <>
              <Typography variant="h6" sx={{ color: "black" }}>
                {selectedProject.NOMBRE}
              </Typography>
              <Typography variant="body1" sx={{ mb: 1, color: "black" }}>
                {selectedProject.DESCRIPCION}
              </Typography>
              <Typography variant="body2" sx={{ color: "black" }}>
                Inicio: {fmtDate(selectedProject.FECHA_INICIO)}
              </Typography>
              <Typography variant="body2" sx={{ color: "black" }}>
                Fin: {fmtDate(selectedProject.FECHA_FIN)}
              </Typography>
              <Typography variant="body2" sx={{ color: "black" }}>
                Estado: {selectedProject.ESTADO}
              </Typography>
              <Typography variant="body2" sx={{ mt: 1, color: "black" }}>
                Avance:
              </Typography>
              <ProgressWithLabel value={selectedProject.AVANCE} />
            </>
          ) : (
            <Typography sx={{ color: "black" }}>Selecciona un proyecto</Typography>
          )}
        </SectionCard>
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
            color: "black",
          }}
        >
          {(() => {
            const localTextFieldStyle = {
              "& .MuiOutlinedInput-root": {
                "& fieldset": { borderColor: "black" },
                "&:hover fieldset": { borderColor: "black" },
                "&.Mui-focused fieldset": { borderColor: "red" },
              },
              "& .MuiInputLabel-root": { color: "black" },
              "& .MuiInputLabel-root.Mui-focused": { color: "red" },
              "& input, & textarea": { color: "black" },
            };
            return (
              <>
                <TextField
                  label="Nombre"
                  name="nombre"
                  variant="outlined"
                  value={newProject.nombre}
                  onChange={handleChange}
                  fullWidth
                  sx={localTextFieldStyle}
                />
                <TextField
                  label="Descripción"
                  name="descripcion"
                  variant="outlined"
                  value={newProject.descripcion}
                  onChange={handleChange}
                  fullWidth
                  multiline
                  minRows={2}
                  sx={localTextFieldStyle}
                />
                <TextField
                  label="Fecha Inicio"
                  name="fechaInicio"
                  type="date"
                  value={newProject.fechaInicio}
                  onChange={handleChange}
                  InputLabelProps={{ shrink: true }}
                  sx={localTextFieldStyle}
                />
                <TextField
                  label="Fecha Fin"
                  name="fechaFin"
                  type="date"
                  value={newProject.fechaFin}
                  onChange={handleChange}
                  InputLabelProps={{ shrink: true }}
                  sx={localTextFieldStyle}
                />
              </>
            );
          })()}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} sx={{ color: "black" }}>
            Cancelar
          </Button>
          <Button
            onClick={handleCreate}
            variant="contained"
            sx={{
              bgcolor: "red",
              "&:hover": { bgcolor: "#b30000" },
            }}
          >
            Guardar
          </Button>
        </DialogActions>
      </Dialog>

      {/* Dialog de confirmación de borrado */}
      <Dialog open={confirmDelete.open} onClose={() => setConfirmDelete({ open: false, id: null })}>
        <DialogTitle sx={{ color: "black" }}>Eliminar proyecto</DialogTitle>
        <DialogContent sx={{ color: "black" }}>
          ¿Seguro que deseas eliminar el proyecto #{confirmDelete.id}?
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmDelete({ open: false, id: null })} sx={{ color: "black" }}>
            Cancelar
          </Button>
          <Button
            variant="contained"
            color="error"
            onClick={() => {
              setProjects((prev) => prev.filter((p) => p.PROYECTO_ID !== confirmDelete.id));
              if (selectedId === confirmDelete.id) {
                setSelectedProject(null);
                setSelectedId(null);
              }
              setConfirmDelete({ open: false, id: null });
              setSnackbar({ open: true, message: "Proyecto eliminado", type: "info" });
            }}
          >
            Eliminar
          </Button>
        </DialogActions>
      </Dialog>

      {/* Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar((s) => ({ ...s, open: false }))}
      >
        <Alert severity={snackbar.type} sx={{ width: "100%" }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Grid>
  );
}
