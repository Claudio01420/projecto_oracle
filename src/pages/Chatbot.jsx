import { Typography, Paper, Stack, TextField, Button, Box } from "@mui/material";

const Bubble = ({ me, text, tag }) => (
  <Box sx={{ display:"flex", justifyContent: me?"flex-end":"flex-start", my: .75 }}>
    <Box sx={{
      maxWidth: 520, p: 1.2, borderRadius: 3,
      bgcolor: me ? "#E1261C" : "#f7f8fa", color: me ? "#fff" : "#111",
      border: me ? "none" : "1px solid #eef0f3"
    }}>
      <div style={{ whiteSpace:"pre-line" }}>{text}</div>
      {tag && <div style={{ fontSize:12, opacity:.6, marginTop:4 }}>{tag}</div>}
    </Box>
  </Box>
);

export default function Chatbot() {
  return (
    <Stack spacing={2}>
      <Typography variant="h4" sx={{ color:"#111", fontWeight:900 }}>Chatbot</Typography>
      <Typography sx={{ color:"#374151" }}>Utiliza “/intro” para una introducción al Chatbot</Typography>

      <Paper sx={{ p:2 }}>
        <Box sx={{ borderBottom:"1px solid #eef0f3", pb:1.5, fontWeight:800, color:"#111" }}>Bot Gestor de Proyectos</Box>
        <Box sx={{ py:1.5 }}>
          <Bubble me={false} text={"Bienvenido a tu gestor de proyectos personal 👋\n¿En qué te puedo ayudar hoy?"} tag="/intro" />
          <Bubble me text="¡Claro!, soy un asistente virtual..." tag="/ayuda" />
          <Bubble me={false} text={"Aquí tienes la lista de mis comandos:\n/ayuda\n/equipos\n/proyectos\n/tareas"} />
        </Box>
        <Box sx={{ display:"flex", gap:1, mt:1 }}>
          <TextField fullWidth placeholder="Mensaje" />
          <Button variant="contained" sx={{ bgcolor:"#E1261C" }}>Enviar</Button>
        </Box>
      </Paper>
    </Stack>
  );
}


