import { Routes, Route, Navigate } from "react-router-dom";
import AppLayout from "./layouts/AppLayout";
import Landing from "./pages/Landing";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";        // default ✓
import Projects from "./pages/Projects";          // default ✓
import Tasks from "./pages/Tasks";                // default ✓
import KPIs from "./pages/KPIs";                  // default ✓
import Chatbot from "./pages/Chatbot";            // default ✓
import Settings from "./pages/Settings";          // default ✓
import Notifications from "./pages/Notifications";// default ✓

export default function App() {
  return (
    <Routes>
      {/* páginas públicas */}
      <Route path="/" element={<Landing />} />
      <Route path="/login" element={<Login />} />

      {/* layout con rutas anidadas (RELATIVAS) */}
      <Route path="/" element={<AppLayout />}>
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="projects" element={<Projects />} />
        <Route path="tasks" element={<Tasks />} />
        <Route path="kpis" element={<KPIs />} />
        <Route path="chatbot" element={<Chatbot />} />
        <Route path="settings" element={<Settings />} />
        <Route path="notifications" element={<Notifications />} />
      </Route>

      {/* fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
