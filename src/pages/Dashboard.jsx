import {
  Box, Paper, Stack, Typography, Button, Chip, LinearProgress, Divider
} from "@mui/material";
import AddRounded from "@mui/icons-material/AddRounded";
import ChecklistRounded from "@mui/icons-material/ChecklistRounded";
import BarChartRounded from "@mui/icons-material/BarChartRounded";
import RocketRounded from "@mui/icons-material/RocketRounded";
import FavoriteRounded from "@mui/icons-material/FavoriteRounded";
import ArrowForwardRounded from "@mui/icons-material/ArrowForwardRounded";
import AccessTimeRounded from "@mui/icons-material/AccessTimeRounded";
import WarningAmberRounded from "@mui/icons-material/WarningAmberRounded";
import EventAvailableRounded from "@mui/icons-material/EventAvailableRounded";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";

const ORACLE_RED = "#E1261C";
const DARK = "#1F2937";
const BG = "#f5f6f8";

/* -------- Animación simple al entrar en viewport -------- */
function RevealOnScroll({ children, delay = 0 }) {
  const ref = useRef(null);
  const [show, setShow] = useState(false);
  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    const io = new IntersectionObserver((entries) => {
      entries.forEach((e) => {
        if (e.isIntersecting) {
          setShow(true);
          io.disconnect();
        }
      });
    }, { threshold: 0.12 });
    io.observe(el);
    return () => io.disconnect();
  }, []);
  return (
    <Box
      ref={ref}
      sx={{
        opacity: show ? 1 : 0,
        transform: show ? "translateY(0)" : "translateY(10px)",
        transition: `opacity .5s ${delay}ms ease, transform .5s ${delay}ms ease`,
      }}
    >
      {children}
    </Box>
  );
}

/* -------- KPI compacta -------- */
function KpiCard({ icon, title, value, sub }) {
  return (
    <Paper sx={{ p: 2, borderRadius: 2, borderLeft: `6px solid ${ORACLE_RED}` }} elevation={2}>
      <Stack direction="row" spacing={1.5} alignItems="center">
        <Box sx={{ color: DARK }}>{icon}</Box>
        <Box sx={{ flex: 1 }}>
          <Typography sx={{ fontWeight: 800, color: "#111" }}>{value}</Typography>
          <Typography variant="body2" sx={{ color: "#6b7280" }}>{title}</Typography>
          {sub && <Typography variant="caption" sx={{ color: "#6b7280" }}>{sub}</Typography>}
        </Box>
      </Stack>
    </Paper>
  );
}

/* -------- Tarjeta de proyecto -------- */
function ProjectRow({ name, desc, status, pct }) {
  return (
    <Paper variant="outlined" sx={{ p: 1.75, borderRadius: 2, bgcolor: "#fff" }}>
      <Stack spacing={1}>
        <Stack direction="row" alignItems="center" justifyContent="space-between">
          <Typography sx={{ fontWeight: 800, color: "#111" }}>{name}</Typography>
          <Chip size="small" label={status}
            sx={{ bgcolor: "#fff1f0", color: ORACLE_RED, border: `1px solid ${ORACLE_RED}22` }} />
        </Stack>
        <Typography sx={{ color: "#6b7280", fontSize: 13 }}>{desc}</Typography>
        <LinearProgress
          variant="determinate"
          value={pct}
          sx={{
            height: 8, borderRadius: 99, bgcolor: "#eceff3",
            "& .MuiLinearProgress-bar": { bgcolor: ORACLE_RED },
          }}
        />
        <Typography sx={{ color: "#6b7280", fontSize: 12 }}>{pct}% completado</Typography>
      </Stack>
    </Paper>
  );
}

/* -------- Panel lateral (justo para la altura de KPIs) -------- */
function RightKpiPanel() {
  return (
    <Paper
      elevation={3}
      sx={{
        p: 2.5,
        borderRadius: 3,
        border: "1px solid #e9edf2",
        bgcolor: "#fff",
        backgroundImage:
          "radial-gradient(1000px 250px at 20% -40%, rgba(225,38,28,0.06), transparent), radial-gradient(600px 200px at 120% 0%, rgba(31,41,55,0.06), transparent)",
      }}
    >
      <Stack direction="row" alignItems="center" spacing={1}>
        <Box sx={{ width: 10, height: 10, bgcolor: ORACLE_RED, borderRadius: 999 }} />
        <Typography sx={{ fontWeight: 900, color: "#111", fontSize: 18 }}>
          Semana en una mirada
        </Typography>
      </Stack>

      {/* Sparkline */}
      <Box sx={{ my: 1.5 }}>
        <Box component="svg" viewBox="0 0 100 40" preserveAspectRatio="none"
             sx={{ width: "100%", height: 64, display: "block" }}>
          <defs>
            <linearGradient id="fillRed" x1="0" x2="0" y1="0" y2="1">
              <stop offset="0%" stopColor="rgba(225,38,28,0.28)" />
              <stop offset="100%" stopColor="rgba(225,38,28,0.02)" />
            </linearGradient>
          </defs>
          <path d="M0,30 C12,16 22,24 34,18 C46,13 56,20 66,14 C76,10 86,22 100,12 L100,40 L0,40 Z"
                fill="url(#fillRed)" />
          <path d="M0,30 C12,16 22,24 34,18 C46,13 56,20 66,14 C76,10 86,22 100,12"
                fill="none" stroke={ORACLE_RED} strokeWidth="2.4" strokeLinejoin="round" strokeLinecap="round" />
        </Box>
        <Stack direction="row" spacing={1.25} sx={{ mt: 1 }}>
          <Chip size="small" label="+5% productividad" sx={{ bgcolor: "#fff1f0", color: ORACLE_RED }} />
          <Chip size="small" label="24 tareas" sx={{ bgcolor: "#f3f4f6", color: "#111" }} />
        </Stack>
      </Box>

      <Divider sx={{ my: 2 }} />

      <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1 }}>
        <EventAvailableRounded sx={{ color: DARK }} />
        <Typography sx={{ fontWeight: 800, color: "#111" }}>Próximos hitos</Typography>
      </Stack>
      <Stack spacing={1.25}>
        <Paper variant="outlined" sx={{ p: 1.25, borderRadius: 2 }}>
          <Stack direction="row" justifyContent="space-between" alignItems="center">
            <Typography sx={{ color: "#374151", fontWeight: 700 }}>Demo Sprint 12</Typography>
            <Stack direction="row" spacing={1} alignItems="center">
              <AccessTimeRounded sx={{ fontSize: 18, color: "#6b7280" }} />
              <Typography sx={{ color: "#6b7280", fontSize: 13 }}>Vie 20</Typography>
            </Stack>
          </Stack>
        </Paper>
        <Paper variant="outlined" sx={{ p: 1.25, borderRadius: 2 }}>
          <Stack direction="row" justifyContent="space-between" alignItems="center">
            <Typography sx={{ color: "#374151", fontWeight: 700 }}>Release 2.0</Typography>
            <Stack direction="row" spacing={1} alignItems="center">
              <AccessTimeRounded sx={{ fontSize: 18, color: "#6b7280" }} />
              <Typography sx={{ color: "#6b7280", fontSize: 13 }}>Mar 24</Typography>
            </Stack>
          </Stack>
        </Paper>
      </Stack>

      <Divider sx={{ my: 2 }} />

      <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1 }}>
        <WarningAmberRounded sx={{ color: ORACLE_RED }} />
        <Typography sx={{ fontWeight: 800, color: "#111" }}>Riesgos del sprint</Typography>
      </Stack>
      <Stack spacing={1}>
        <Stack direction="row" justifyContent="space-between" sx={{ color: "#374151", fontSize: 14 }}>
          <span>2 tareas bloqueadas</span>
          <Chip size="small" label="Revisar hoy" sx={{ bgcolor: "#fff1f0", color: ORACLE_RED }} />
        </Stack>
      </Stack>
    </Paper>
  );
}

/* ===================== DASHBOARD ===================== */
export default function Dashboard() {
  const nav = useNavigate();

  return (
    <Box sx={{ bgcolor: BG }}>
      {/* HERO */}
      <RevealOnScroll>
        <Paper sx={{ p: 3, borderRadius: 3, border: "1px solid #e9edf2", bgcolor: "#fff", mb: 2 }} elevation={0}>
          <Stack spacing={1}>
            <Typography variant="h4" sx={{ fontWeight: 900, color: "#111" }}>Bienvenido 👋</Typography>
            <Typography sx={{ color: "#6b7280" }}>
              Recorre los indicadores y atajos en orden. Te mostramos lo esencial primero.
            </Typography>
            <Stack direction={{ xs: "column", sm: "row" }} spacing={1.25} sx={{ mt: 1 }}>
              <Button variant="contained" startIcon={<AddRounded />}
                sx={{ bgcolor: ORACLE_RED, "&:hover": { bgcolor: "#c41f15" } }}
                onClick={() => nav("/projects")}
              >Crear proyecto</Button>
              <Button variant="outlined" startIcon={<ChecklistRounded />}
                sx={{ borderColor: "#d6d9de", color: "#111" }}
                onClick={() => nav("/tasks")}
              >Ver tareas</Button>
              <Button variant="outlined" startIcon={<BarChartRounded />}
                sx={{ borderColor: "#d6d9de", color: "#111" }}
                onClick={() => nav("/kpis")}
              >Ver KPIs</Button>
              <Button variant="outlined" startIcon={<RocketRounded />}
                sx={{ borderColor: "#d6d9de", color: "#111" }}
                onClick={() => nav("/chatbot")}
              >Abrir Chatbot</Button>
            </Stack>
          </Stack>
        </Paper>
      </RevealOnScroll>

      {/* ======= TOP: SOLO KPIs + PANEL ======= */}
      <Box
        sx={{
          display: "grid",
          gridTemplateColumns: { xs: "1fr", md: "minmax(0,1fr) 440px", lg: "minmax(0,1fr) 480px" },
          gap: { xs: 2, md: 2.5 },
          alignItems: "start",
          mb: 2, // separación justa respecto a la siguiente banda
        }}
      >
        {/* Columna izquierda: SOLO KPIs */}
        <RevealOnScroll delay={60}>
          <Stack spacing={1.25}>
            <KpiCard icon={<BarChartRounded />}   title="Productividad" value="87%" sub="+5% esta semana" />
            <KpiCard icon={<ChecklistRounded />}  title="Tareas activas" value="24" sub="8 completadas hoy" />
            <KpiCard icon={<RocketRounded />}      title="Sprint actual" value="Sprint 12" sub="5 días restantes" />
            <KpiCard icon={<FavoriteRounded />}    title="Burnout Risk" value="Bajo" sub="Equipo saludable" />
          </Stack>
        </RevealOnScroll>

        {/* Columna derecha: Panel (misma “banda” que KPIs) */}
        <Box sx={{ display: { xs: "none", md: "block" } }}>
          <RightKpiPanel />
        </Box>
      </Box>

      {/* ======= DEBAJO: TODO AL 100% DE ANCHO ======= */}
      <RevealOnScroll delay={140}>
        <Paper sx={{ p: 2.5, borderRadius: 3, border: "1px solid #e9edf2", bgcolor: "#fff", mb: 2 }}>
          <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ mb: 1 }}>
            <Typography variant="h6" sx={{ fontWeight: 900, color: "#111" }}>Proyectos activos</Typography>
            <Button size="small" endIcon={<ArrowForwardRounded />} sx={{ color: ORACLE_RED, textTransform: "none" }}
              onClick={() => nav("/projects")}
            >Ir a proyectos</Button>
          </Stack>
          <Stack spacing={1.25}>
            <ProjectRow name="Sistema de Facturación v2.0" desc="Backend API y Frontend React" status="En Progreso" pct={75} />
            <ProjectRow name="Migración Base de Datos"       desc="Oracle 19c a Oracle 21c"       status="Planificación" pct={25} />
          </Stack>
        </Paper>
      </RevealOnScroll>

      <RevealOnScroll delay={200}>
        <Paper sx={{ p: 2.5, borderRadius: 3, border: "1px solid #e9edf2", bgcolor: "#fff", mb: 2 }}>
          <Typography variant="h6" sx={{ fontWeight: 900, color: "#111", mb: 1 }}>
            Tareas recientes
          </Typography>
          <Stack spacing={1}>
            {[
              { dot: "#10B981", title: "Implementar autenticación JWT", who: "María González", badge: "Completada", color: "#10B981" },
              { dot: "#2563EB", title: "Diseño UI dashboard admin",      who: "Carlos Ruiz",     badge: "En progreso", color: "#2563EB" },
              { dot: "#F59E0B", title: "Testing integración API",        who: "Ana López",       badge: "Pendiente",  color: "#F59E0B" },
            ].map((t, i) => (
              <Paper key={i} variant="outlined" sx={{ p: 1.5, borderRadius: 2, bgcolor: "#fafbfc" }}>
                <Stack direction="row" spacing={1.25} alignItems="center">
                  <Box sx={{ width: 10, height: 10, borderRadius: 99, bgcolor: t.dot }} />
                  <Box sx={{ flex: 1 }}>
                    <Typography sx={{ color: "#111", fontWeight: 700 }}>{t.title}</Typography>
                    <Typography sx={{ color: "#6b7280", fontSize: 13 }}>Asignado a: {t.who}</Typography>
                  </Box>
                  <Chip size="small" label={t.badge} sx={{ bgcolor: `${t.color}22`, color: t.color }} />
                </Stack>
              </Paper>
            ))}
          </Stack>

          <Divider sx={{ my: 2 }} />
          <Button variant="outlined" onClick={() => nav("/tasks")} sx={{ borderColor: "#d6d9de", color: "#111" }}>
            Ver todas las tareas
          </Button>
        </Paper>
      </RevealOnScroll>

      <RevealOnScroll delay={240}>
        <Paper sx={{ p: 2.5, borderRadius: 3, border: "1px solid #e9edf2", bgcolor: "#fff", mb: 4 }}>
          <Stack direction={{ xs: "column", sm: "row" }}
                 alignItems={{ xs: "flex-start", sm: "center" }}
                 justifyContent="space-between" spacing={1.25}>
            <Box>
              <Typography sx={{ fontWeight: 800, color: "#111" }}>
                ¿Quieres insights más detallados?
              </Typography>
              <Typography sx={{ color: "#6b7280" }}>
                Revisa los KPIs para detectar riesgos y oportunidades.
              </Typography>
            </Box>
            <Button variant="contained"
                    sx={{ bgcolor: ORACLE_RED, "&:hover": { bgcolor: "#c41f15" } }}
                    endIcon={<BarChartRounded />} onClick={() => nav("/kpis")}>
              Abrir KPIs
            </Button>
          </Stack>
        </Paper>
      </RevealOnScroll>
    </Box>
  );
}
