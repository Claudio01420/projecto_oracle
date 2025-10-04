// MtdrSpring/frontend/src/services/tasksApi.js
const API = import.meta.env.VITE_API_URL ?? "/api";

export async function createTask(task) {
  const res = await fetch(`${API}/tasks`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(task),
  });
  if (!res.ok) {
    const msg = await res.text();
    throw new Error(msg || "Error creating task");
  }
  return res.json();
}
