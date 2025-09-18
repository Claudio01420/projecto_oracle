import { Typography, Paper, Stack, TextField, Button, Box } from "@mui/material";
import SendRounded from "@mui/icons-material/SendRounded";
import { useState } from "react";

const Bubble = ({ me, text, tag }) => (
  <Box sx={{ display:"flex", justifyContent: me?"flex-end":"flex-start", my: .75 }}>
    <Box sx={{
      maxWidth: 520, p: 1.2, borderRadius: 3,
      bgcolor: me ? "#E1261C" : "#ffffff", color: me ? "#fff" : "#111",
      border: me ? "none" : "1px solid #eef0f3", boxShadow: me ? "0 4px 14px rgba(225,38,28,.2)" : "0 2px 8px rgba(0,0,0,.04)"
    }}>
      <div style={{ whiteSpace:"pre-line" }}>{text}</div>
      {tag && <div style={{ fontSize:12, opacity:.6, marginTop:4 }}>{tag}</div>}
    </Box>
  </Box>
);

export default function Chatbot() {
  const [msg, setMsg] = useState("");

  return (
    <Stack spacing={2}>
      <Typography variant="h4" sx={{ color:"#111", fontWeight:900 }}>Chatbot</Typography>
      <Typography sx={{ color:"#6b7280", mb:1 }}>Usa /ayuda, /proyectos, /tareas…</Typography>

      <Paper sx={{
        p: 0, overflow: "hidden",
        background: "linear-gradient(180deg, #ffffff 0%, #fafbfc 100%)",
        border: "1px solid #e9edf2"
      }}>
        <Box sx={{ px:2, py:1.5, borderBottom:"1px solid #e9edf2", fontWeight:800, color:"#111" }}>
          Bot Gestor de Proyectos
        </Box>

        {/* Historial */}
        <Box sx={{ px:2, py:1.5, minHeight: 280 }}>
          <Bubble me={false} text={"Hola 👋 ¿en qué te ayudo?"} tag="/intro" />
          <Bubble me text="Muéstrame proyectos activos" />
          <Bubble me={false} text={"Proyectos:\n• Alfa (70%)\n• Bravo (0%)\n• Chatfy (100%)"} />
        </Box>

        {/* Input sticky */}
        <Box sx={{ p: 1.25, borderTop:"1px solid #e9edf2", bgcolor:"#fff", display:"flex", gap:1 }}>
          <TextField fullWidth size="small" value={msg} onChange={e=>setMsg(e.target.value)} placeholder="Escribe un mensaje…" />
          <Button variant="contained" sx={{ bgcolor:"#E1261C" }} endIcon={<SendRounded />}>Enviar</Button>
        </Box>
      </Paper>
    </Stack>
  );
}



