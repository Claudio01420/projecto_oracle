import { Card, CardContent, Typography } from "@mui/material";

export default function StatTile({ title, value, subtitle }) {
  return (
    <Card>
      <CardContent>
        <Typography variant="overline" sx={{ color: "#6b7280" }}>{title}</Typography>
        <Typography variant="h5" sx={{ fontWeight: 800, color: "#111" }}>{value}</Typography>
        {subtitle && <Typography variant="body2" sx={{ color: "#6b7280" }}>{subtitle}</Typography>}
      </CardContent>
    </Card>
  );
}

