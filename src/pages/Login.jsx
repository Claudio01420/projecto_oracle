import { Paper, TextField, Button, Stack, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const nav = useNavigate();
  return (
    <Stack alignItems="center" sx={{ py: 6 }}>
      <Paper sx={{ p: 3, width: "100%", maxWidth: 420 }}>
        <Typography variant="h5" fontWeight={700} sx={{ mb: 2 }}>Iniciar sesión</Typography>
        <Stack spacing={2}>
          <TextField label="Email" fullWidth />
          <TextField label="Password" type="password" fullWidth />
          <Button variant="contained" onClick={() => nav("/dashboard")}>
            Entrar
          </Button>
        </Stack>
      </Paper>
    </Stack>
  );
}

