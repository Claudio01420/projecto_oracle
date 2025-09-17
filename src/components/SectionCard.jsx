import { Paper, Typography, Box } from "@mui/material";

export default function SectionCard({ title, children, actions }) {
  return (
    <Paper sx={{ p: 2.5 }}>
      <Box sx={{ display: "flex", alignItems: "center", mb: 2 }}>
        <Typography variant="h6" sx={{ fontWeight: 800, color: "#111", flex: 1 }}>{title}</Typography>
        {actions}
      </Box>
      {children}
    </Paper>
  );
}

