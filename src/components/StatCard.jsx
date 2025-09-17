import { Paper } from "@mui/material";

export default function StatCard({ title, value, sub }) {
  return (
    <Paper className="p-4">
      <div className="text-xs uppercase tracking-wide text-gray-400">{title}</div>
      <div className="text-2xl font-semibold mt-1">{value}</div>
      {sub && <div className="text-xs text-gray-400 mt-1">{sub}</div>}
    </Paper>
  );
}
