import { Button } from "@mui/material";

export default function Topbar() {
  return (
    <header className="h-14 border-b border-white/5 px-4 flex items-center justify-between bg-[#0f1115]/60 backdrop-blur">
      <div className="text-sm text-gray-400">Project Management System</div>
      <div className="flex items-center gap-2">
        <Button variant="contained" size="small">+ Nuevo proyecto</Button>
      </div>
    </header>
  );
}
