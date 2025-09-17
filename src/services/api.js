const BASE = import.meta.env.VITE_API_URL || "http://localhost:8080";

export async function listTodos() {
  const r = await fetch(`${BASE}/todolist`);
  if (!r.ok) throw new Error(`GET /todolist -> ${r.status}`);
  return r.json();
}

export async function createTodo(body) {
  const r = await fetch(`${BASE}/todolist`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (!r.ok) throw new Error(`POST /todolist -> ${r.status}`);
  return r.json();
}

export async function deleteTodo(id) {
  const r = await fetch(`${BASE}/todolist/${id}`, { method: "DELETE" });
  if (!r.ok && r.status !== 204) throw new Error(`DELETE /todolist/${id} -> ${r.status}`);
}
