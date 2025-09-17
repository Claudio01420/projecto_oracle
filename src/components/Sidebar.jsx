import { NavLink } from "react-router-dom";
import { Dashboard, Assignment, Folder, ShowChart, Chat, Settings, Notifications } from "@mui/icons-material";

const items = [
  { to: "/dashboard", icon: <Dashboard fontSize="small" />, label: "Dashboard" },
  { to: "/projects",  icon: <Folder fontSize="small" />,     label: "Proyectos" },
  { to: "/tasks",     icon: <Assignment fontSize="small" />, label: "Tareas" },
  { to: "/kpis",      icon: <ShowChart fontSize="small" />,  label: "KPIs" },
  { to: "/chatbot",   icon: <Chat fontSize="small" />,       label: "Chatbot" },
  { to: "/notifications", icon: <Notifications fontSize="small" />, label: "Notificaciones" },
  { to: "/settings",  icon: <Settings fontSize="small" />,   label: "Ajustes" },
];

export default function Sidebar() {
  return (
    <aside className="w-64 bg-[#121521] border-r border-white/5 hidden md:block">
      <div className="px-5 py-4 text-[15px] font-semibold tracking-wide">
        <span className="text-white">ORACLE</span>
      </div>
      <nav className="px-2">
        {items.map(i => (
          <NavLink
            key={i.to}
            to={i.to}
            className={({isActive}) =>
              `flex items-center gap-3 px-4 py-3 rounded-lg mb-1 text-sm
               ${isActive ? "bg-blue-600/20 text-blue-300" : "text-gray-300 hover:bg-white/5"}`
            }>
            {i.icon}
            {i.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
