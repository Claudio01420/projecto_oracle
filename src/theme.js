import { createTheme } from "@mui/material/styles";

const oracleRed = "#E1261C";
const bg = "#0f1115";           // fondo oscuro suave
const surface = "#11141a";      // paneles oscuros
const paper = "#ffffff";        // tarjetas blancas

const theme = createTheme({
  palette: {
    mode: "dark",
    primary: { main: oracleRed },
    background: { default: bg, paper: surface },
    text: { primary: "#fff", secondary: "rgba(255,255,255,.7)" },
    divider: "rgba(255,255,255,.08)",
  },
  typography: {
    fontFamily: `'Inter', system-ui, -apple-system, Segoe UI, Roboto, Arial`,
    h1: { fontWeight: 800 }, h2: { fontWeight: 800 }, h3: { fontWeight: 800 }, h4: { fontWeight: 800 },
  },
  shape: { borderRadius: 12 },
  components: {
    MuiPaper: { styleOverrides: { root: { backgroundColor: paper, color: "#111", border: "1px solid #eef0f3" } } },
    MuiCard: { styleOverrides: { root: { border: "1px solid #eef0f3" } } },
    MuiAppBar: { styleOverrides: { root: { backdropFilter: "saturate(120%) blur(6px)" } } },
    MuiButton: { defaultProps: { disableElevation: true } },
    MuiContainer: { styleOverrides: { root: { maxWidth: "1200px!important" } } },
  },
});

export default theme;

