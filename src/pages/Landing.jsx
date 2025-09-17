import {
  Box,
  Button,
  Container,
  Grid,
  Stack,
  Typography,
  Paper,
  Card,
  CardContent,
} from "@mui/material";
import {
  TimelineRounded,
  SmartToyRounded,
  InsightsRounded,
  SecurityRounded,
  ArrowForwardRounded,
} from "@mui/icons-material";
import { Link, useNavigate } from "react-router-dom";

const features = [
  { icon: <TimelineRounded />, title: "Gestión de proyectos", desc: "Crea, asigna y da seguimiento con claridad." },
  { icon: <SmartToyRounded />, title: "Chatbot (Telegram)", desc: "Automatiza recordatorios y consultas rápidas." },
  { icon: <InsightsRounded />, title: "KPIs de productividad", desc: "Métricas accionables en tiempo real." },
  { icon: <SecurityRounded />, title: "Seguridad Oracle Cloud", desc: "Autenticación y datos protegidos en OCI." },
];

export default function Landing() {
  const nav = useNavigate();

  return (
    <Box sx={{ bgcolor: "#f4f6f9" }}>
      {/* HERO */}
      <Box
        sx={{
          background: "linear-gradient(180deg, #f4f6f9 0%, #eef1f6 100%)",
          borderBottom: "1px solid #e9edf2",
          pt: { xs: 6, md: 10 },
          pb: { xs: 6, md: 10 },
        }}
      >
        <Container>
          <Grid container spacing={6} alignItems="center">
            {/* Texto */}
            <Grid item xs={12} md={6}>
              <Stack spacing={3}>
                <Typography
                  variant="h2"
                  sx={{ fontWeight: 900, lineHeight: 1.1, color: "#111" }}
                >
                  Project Management
                  <br /> System
                </Typography>

                <Typography sx={{ color: "#374151", maxWidth: 620 }}>
                  Centraliza tus proyectos y tareas en OCI. Automatiza notificaciones con Telegram,
                  mide productividad y toma decisiones con datos.
                </Typography>

                <Stack direction="row" spacing={2} alignItems="center">
                  <Button
                    variant="contained"
                    size="large"
                    onClick={() => nav("/dashboard")}
                    sx={{
                      bgcolor: "#E1261C",
                      px: 3,
                      "&:hover": { bgcolor: "#c21f17" },
                    }}
                    endIcon={<ArrowForwardRounded />}
                  >
                    Probar demo
                  </Button>
                  <Button
                    component={Link}
                    to="/login"
                    size="large"
                    variant="outlined"
                    sx={{ color: "#111", borderColor: "#d6d9de" }}
                  >
                    Log in
                  </Button>
                </Stack>

                {/* Stats */}
                <Stack
                  direction={{ xs: "column", sm: "row" }}
                  spacing={3}
                  sx={{ pt: 1 }}
                >
                  <Stat label="Aumento de productividad" value="+20%" />
                  <Stat label="Automatización con bot" value="100%" />
                  <Stat label="Disponibilidad en OCI" value="99.8%" />
                </Stack>
              </Stack>
            </Grid>

            {/* Mockup */}
            <Grid item xs={12} md={6}>
              <Box
                sx={{
                  width: "100%",
                  display: "flex",
                  justifyContent: { xs: "center", md: "flex-end" },
                }}
              >
                <LaptopMockup src="/fotolanding.png" alt="Vista previa" />
              </Box>
            </Grid>
          </Grid>
        </Container>
      </Box>

      {/* FEATURES */}
      <Container sx={{ py: { xs: 6, md: 8 } }}>
        <Typography variant="h4" sx={{ color: "#111", fontWeight: 900, mb: 3 }}>
          Funciones Principales
        </Typography>

        <Grid container spacing={2}>
          {features.map((f, i) => (
            <Grid item xs={12} sm={6} md={3} key={i}>
              <Card
                sx={{
                  height: "100%",
                  border: "1px solid #eef0f3",
                  "&:hover": { boxShadow: "0 8px 24px rgba(0,0,0,.06)" },
                }}
              >
                <CardContent>
                  <Box
                    sx={{
                      width: 44, height: 44, borderRadius: 2,
                      display: "grid", placeItems: "center",
                      bgcolor: "#f1f5f9", color: "#111", mb: 1.5,
                    }}
                  >
                    {f.icon}
                  </Box>
                  <Typography variant="subtitle1" sx={{ fontWeight: 800, color: "#111" }}>
                    {f.title}
                  </Typography>
                  <Typography sx={{ color: "#6b7280" }}>{f.desc}</Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Container>

      {/* CTA FINAL */}
      <Container sx={{ pb: { xs: 8, md: 10 } }}>
        <Paper
          sx={{
            p: { xs: 3, md: 4 },
            display: "flex",
            alignItems: { xs: "start", md: "center" },
            gap: 3,
            border: "1px solid #e9edf2",
            background: "linear-gradient(180deg, #ffffff 0%, #fafbfc 100%)",
          }}
        >
          <Box sx={{ flex: 1 }}>
            <Typography variant="h5" sx={{ fontWeight: 900, color: "#111" }}>
              ¿Listo para acelerar tus sprints?
            </Typography>
            <Typography sx={{ color: "#6b7280" }}>
              Prueba el dashboard con datos de ejemplo y configura tu primer proyecto en minutos.
            </Typography>
          </Box>
          <Button
            variant="contained"
            onClick={() => nav("/dashboard")}
            sx={{ bgcolor: "#E1261C", px: 3, "&:hover": { bgcolor: "#c21f17" } }}
          >
            Empezar ahora
          </Button>
        </Paper>
      </Container>
    </Box>
  );
}

/* --- subcomponentes --- */

function Stat({ label, value }) {
  return (
    <Stack direction="row" spacing={1} alignItems="baseline">
      <Typography sx={{ fontWeight: 900, color: "#111", fontSize: 22 }}>
        {value}
      </Typography>
      <Typography sx={{ color: "#6b7280" }}>{label}</Typography>
    </Stack>
  );
}

function LaptopMockup({ src, alt }) {
  return (
    <Box
      sx={{
        borderRadius: "12px",
        bgcolor: "#111",
        p: "8px 8px 0 8px",           // marco superior
        boxShadow: "0 12px 32px rgba(0,0,0,.18)",
        border: "1px solid #e5e7eb",
        display: "inline-block",
        width: "100%",
        maxWidth: 560,
      }}
    >
      <Box
        component="img"
        src={src}
        alt={alt}
        sx={{
          display: "block",
          width: "100%",
          borderRadius: "0 0 8px 8px", // esquinas inferiores
        }}
      />
    </Box>
  );
}
