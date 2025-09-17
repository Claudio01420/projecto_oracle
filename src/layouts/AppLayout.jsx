import { Outlet, Link, useLocation } from "react-router-dom";
import { AppBar, Toolbar, Box, Button, Container, Drawer, List, ListItemButton, ListItemText, Divider } from "@mui/material";
import HomeRoundedIcon from "@mui/icons-material/HomeRounded";

const nav = [
  { to: "/dashboard", label: "Dashboard" },
  { to: "/projects", label: "Proyectos" },
  { to: "/tasks", label: "Tareas" },
  { to: "/kpis", label: "KPIs" },
  { to: "/chatbot", label: "Chatbot" },
  { to: "/notifications", label: "Notificaciones" },
  { to: "/settings", label: "Ajustes" },
];

export default function AppLayout() {
  const { pathname } = useLocation();

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: "#f4f6f9" }}>
      {/* Topbar */}
      <AppBar position="sticky" color="transparent" elevation={0} sx={{ borderBottom: "1px solid #eef0f3" }}>
        <Toolbar sx={{ gap: 2 }}>
          <HomeRoundedIcon sx={{ color: "#E1261C" }} />
          <Box sx={{ fontWeight: 900, color: "#111" }}>ORACLE</Box>
          <Box sx={{ flex: 1 }} />
          <Button component={Link} to="/" variant="text" size="small" sx={{ color: "#111" }}>
            Home
          </Button>
          <Button component={Link} to="/login" size="small" variant="outlined" sx={{ color: "#111", borderColor: "#d6d9de" }}>
            Login
          </Button>
        </Toolbar>
      </AppBar>

      {/* Layout: sidebar izquierda + contenido */}
      <Box sx={{ display: "grid", gridTemplateColumns: "260px 1fr", minHeight: "calc(100vh - 64px)" }}>
        <Drawer variant="permanent" open
          PaperProps={{ sx: { position: "relative", width: 260, p: 0, bgcolor: "#11141a", color: "#fff", borderRight: "1px solid rgba(255,255,255,.08)" } }}>
          <Box sx={{ px: 2, py: 2, fontWeight: 800, opacity: .9 }}>MENÚ</Box>
          <Divider sx={{ borderColor: "rgba(255,255,255,.08)" }} />
          <List dense>
            {nav.map(item => (
              <ListItemButton
                key={item.to}
                component={Link}
                to={item.to}
                selected={pathname === item.to}
                sx={{
                  borderRadius: 1, mx: 1, my: .5,
                  "&.Mui-selected": { bgcolor: "rgba(225,38,28,.14)" }
                }}>
                <ListItemText primary={item.label} />
              </ListItemButton>
            ))}
          </List>
        </Drawer>

        <Container sx={{ py: 4 }}>
          <Outlet />
        </Container>
      </Box>
    </Box>
  );
}
