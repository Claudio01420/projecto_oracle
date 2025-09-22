import { Outlet, Link, useLocation } from "react-router-dom";
import {
  AppBar, Toolbar, Box, Button, Container, Drawer, List, ListItemButton,
  ListItemText, Avatar, Tooltip
} from "@mui/material";
import DashboardRounded from "@mui/icons-material/DashboardRounded";
import AssignmentRounded from "@mui/icons-material/AssignmentRounded";
import FolderRounded from "@mui/icons-material/FolderRounded";
import InsightsRounded from "@mui/icons-material/InsightsRounded";
import ChatRounded from "@mui/icons-material/ChatRounded";
import NotificationsRounded from "@mui/icons-material/NotificationsRounded";
import SettingsRounded from "@mui/icons-material/SettingsRounded";

const NAV_BG = "#1b1b1b";     
const NAV_BORDER = "#2a2a2a";  
const APPBAR_H = 64;           

const nav = [
  { to: "/dashboard", label: "Dashboard", icon: <DashboardRounded fontSize="small" /> },
  { to: "/projects",  label: "Proyectos", icon: <FolderRounded fontSize="small" /> },
  { to: "/tasks",     label: "Tareas",    icon: <AssignmentRounded fontSize="small" /> },
  { to: "/kpis",      label: "KPIs",      icon: <InsightsRounded fontSize="small" /> },
  { to: "/chatbot",   label: "Chatbot",   icon: <ChatRounded fontSize="small" /> },
  { to: "/notifications", label: "Notificaciones", icon: <NotificationsRounded fontSize="small" /> },
  { to: "/settings",  label: "Ajustes",   icon: <SettingsRounded fontSize="small" /> },
];

export default function AppLayout() {
  const { pathname } = useLocation();

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: "#f4f6f9" }}>
      {}
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          bgcolor: NAV_BG,
          color: "#fff",
          borderBottom: `1px solid ${NAV_BORDER}`,
          height: APPBAR_H,
          justifyContent: "center", 
        }}
      >
        <Toolbar sx={{ gap: 2, minHeight: APPBAR_H }}>
          {}
          <Box sx={{ display: "flex", alignItems: "center" }}>
            <Box
              component="img"
              src="/Oracle_logo.png"
              alt="Oracle"
              sx={{ height: 24, display: "block" }}
            />
          </Box>

          <Box sx={{ flex: 1 }} />
          <Button
            component={Link}
            to="/"
            size="small"
            sx={{ color: "#fff", textTransform: "none", opacity: .9, "&:hover": { opacity: 1 } }}
          >
            Home
          </Button>
          <Tooltip title="Mi perfil">
            <Avatar src="/avatar.png" alt="User" sx={{ width: 30, height: 30 }} />
          </Tooltip>
        </Toolbar>
      </AppBar>

      {}
      <Drawer
        variant="permanent"
        open
        sx={{
          display: { xs: "none", md: "block" },
          width: "var(--sidebar-w)", 
          flexShrink: 0,
          "& .MuiDrawer-paper": {
            width: "var(--sidebar-w)",
            boxSizing: "border-box",
            bgcolor: NAV_BG,
            color: "#fff",
            borderRight: `1px solid ${NAV_BORDER}`,
            top: APPBAR_H,                         
            height: `calc(100% - ${APPBAR_H}px)`,  
          },
        }}
      >
        <Box sx={{ px: 2, py: 2, fontWeight: 800, letterSpacing: .3, opacity: .9 }}>
          MENÚ
        </Box>

        <List dense sx={{ px: 1.25, py: .5 }}>
          {nav.map((item) => {
            const selected = pathname === item.to;
            return (
              <ListItemButton
                key={item.to}
                component={Link}
                to={item.to}
                selected={selected}
                sx={{
                  borderRadius: 10,
                  mb: .75,
                  px: 1.25,
                  color: "#fff",
                  bgcolor: selected ? "rgba(225,38,28,.22)" : "transparent",
                  "& .MuiListItemText-primary": {
                    fontWeight: selected ? 800 : 600,
                    letterSpacing: .2
                  },
                  "&:hover": {
                    bgcolor: selected ? "rgba(225,38,28,.28)" : "rgba(255,255,255,.06)"
                  },
                  transition: "background-color .15s ease",
                }}
              >
                <Box sx={{ mr: 1.25, display: "grid", placeItems: "center" }}>
                  {item.icon}
                </Box>
                <ListItemText primary={item.label} />
              </ListItemButton>
            );
          })}
        </List>
      </Drawer>

      {}
      <Box
        component="main"
        sx={{
          ml: { md: "var(--sidebar-w)" }, 
        }}
      >
        {}
        <Toolbar sx={{ minHeight: APPBAR_H }} />
        <Container sx={{ py: 4 }}>
          <Outlet />
        </Container>
      </Box>

      {}
      <Box className="bp-badge" />
    </Box>
  );
}

