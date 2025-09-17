import { Button, Container, Grid, Stack, Typography, Box, Paper } from "@mui/material";
import { Link, useNavigate } from "react-router-dom";

export default function Landing() {
  const nav = useNavigate();
  return (
    <Container sx={{ py: 8 }}>
      <Grid container spacing={6} alignItems="center">
        {/* Texto lado izquierdo */}
        <Grid item xs={12} md={6}>
          <Stack spacing={3}>
            <Typography variant="h3" sx={{ fontWeight: 900, color: "#111" }}>
              Project Management System
            </Typography>
            <Typography sx={{ color: "#374151" }}>
              Centraliza tus proyectos y tareas en OCI. Automatiza notificaciones con Telegram, mide productividad y toma decisiones con datos.
            </Typography>
            <Stack direction="row" spacing={2}>
              <Button
                variant="contained"
                onClick={() => nav("/dashboard")}
                sx={{ bgcolor: "#E1261C" }}
              >
                Probar Demo
              </Button>
              <Button
                variant="outlined"
                component={Link}
                to="/login"
                sx={{ color: "#111", borderColor: "#d6d9de" }}
              >
                Log In
              </Button>
            </Stack>
          </Stack>
        </Grid>

        {/* Imagen lado derecho con estilo mockup */}
        <Grid item xs={12} md={6}>
          <Box
            sx={{
              borderRadius: "12px",
              bgcolor: "#111",
              p: "8px 8px 0 8px", // borde superior simulando marco
              boxShadow: "0 8px 24px rgba(0,0,0,.15)",
              border: "1px solid #e5e7eb",
              display: "inline-block",
              width: "100%",
              maxWidth: 500,
            }}
          >
            <Box
              component="img"
              src="/fotolanding.png"
              alt="Vista previa"
              sx={{
                display: "block",
                width: "100%",
                borderRadius: "0 0 8px 8px", // redondeado solo abajo
              }}
            />
          </Box>
        </Grid>
      </Grid>

      {/* Sección de funciones */}
      <Stack spacing={2} sx={{ mt: 8 }}>
        <Typography variant="h5" sx={{ color: "#111", fontWeight: 800 }}>
          Funciones Principales
        </Typography>
        <Grid container spacing={2}>
          {[
            "Gestión de proyectos",
            "Chatbot (Telegram)",
            "KPIs de productividad",
            "Seguridad Oracle Cloud",
          ].map((x) => (
            <Grid item xs={12} md={3} key={x}>
              <Paper sx={{ p: 2 }}>{x}</Paper>
            </Grid>
          ))}
        </Grid>
      </Stack>
    </Container>
  );
}

