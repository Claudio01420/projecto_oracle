import { Paper } from "@mui/material";
import { ResponsiveContainer, LineChart, Line, CartesianGrid, XAxis, YAxis, BarChart, Bar, Tooltip } from "recharts";

export function LineChartCard({ title, data }) {
  return (
    <Paper className="p-4 h-64">
      <div className="text-sm text-gray-300 mb-3">{title}</div>
      <ResponsiveContainer width="100%" height="85%">
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="#263043" />
          <XAxis dataKey="name" stroke="#9ca3af" />
          <YAxis stroke="#9ca3af" />
          <Tooltip />
          <Line type="monotone" dataKey="real" stroke="#60a5fa" strokeWidth={2} />
          <Line type="monotone" dataKey="plan" stroke="#f87171" strokeWidth={2} />
        </LineChart>
      </ResponsiveContainer>
    </Paper>
  );
}

export function BarChartCard({ title, data }) {
  return (
    <Paper className="p-4 h-64">
      <div className="text-sm text-gray-300 mb-3">{title}</div>
      <ResponsiveContainer width="100%" height="85%">
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="#263043" />
          <XAxis dataKey="name" stroke="#9ca3af" />
          <YAxis stroke="#9ca3af" />
          <Tooltip />
          <Bar dataKey="value" />
        </BarChart>
      </ResponsiveContainer>
    </Paper>
  );
}
