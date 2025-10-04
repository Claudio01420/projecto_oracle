import { useState } from "react";
import { validateTask } from "../../validation/taskSchema";
import { createTask } from "../../services/tasksApi";

export default function AddTaskDialog({ open, onClose, onCreated, currentUser, sprintOptions=[], devOptions=[] }) {
  const [form, setForm] = useState({
    title: "", description: "", estimatedHours: 1,
    assigneeId: "", status: "todo", priority: "medium",
    sprintId: "", tags: [], dueDate: ""
  });
  const [submitting, setSubmitting] = useState(false);
  const [errors, setErrors] = useState({});

  const setField = (k, v) => setForm(p => ({ ...p, [k]: v }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validateTask(form);
    setErrors(errs);
    if (Object.keys(errs).length) return;
    setSubmitting(true);
    try {
      const payload = {
        ...form,
        role: "developer",
        createdBy: currentUser?.id
      };
      const created = await createTask(payload);
      onCreated?.(created);
      onClose?.();
    } catch (err) {
      alert(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  if (!open) return null;

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white w-full max-w-lg rounded-2xl shadow-xl p-5">
        <div className="flex items-center justify-between mb-3">
          <h3 className="text-xl font-bold">Nueva tarea (Developer)</h3>
          <button onClick={onClose} className="text-gray-500 hover:text-gray-800">✕</button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-3">
          <div>
            <label className="block text-sm font-semibold">Título</label>
            <input className="w-full border rounded-xl px-3 py-2"
              value={form.title} onChange={e=>setField("title", e.target.value)} />
            {errors.title && <p className="text-red-600 text-sm mt-1">{errors.title}</p>}
          </div>

          <div>
            <label className="block text-sm font-semibold">Descripción</label>
            <textarea className="w-full border rounded-xl px-3 py-2"
              rows={3} value={form.description} onChange={e=>setField("description", e.target.value)} />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-semibold">Horas estimadas (≤4)</label>
              <input type="number" min="0.5" step="0.5" className="w-full border rounded-xl px-3 py-2"
                value={form.estimatedHours} onChange={e=>setField("estimatedHours", Number(e.target.value))} />
              {errors.estimatedHours && <p className="text-red-600 text-sm mt-1">{errors.estimatedHours}</p>}
            </div>
            <div>
              <label className="block text-sm font-semibold">Prioridad</label>
              <select className="w-full border rounded-xl px-3 py-2"
                value={form.priority} onChange={e=>setField("priority", e.target.value)}>
                <option value="low">Baja</option>
                <option value="medium">Media</option>
                <option value="high">Alta</option>
              </select>
              {errors.priority && <p className="text-red-600 text-sm mt-1">{errors.priority}</p>}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-semibold">Estado</label>
              <select className="w-full border rounded-xl px-3 py-2"
                value={form.status} onChange={e=>setField("status", e.target.value)}>
                <option value="todo">To Do</option>
                <option value="in_progress">In Progress</option>
                <option value="done">Done</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-semibold">Sprint</label>
              <select className="w-full border rounded-xl px-3 py-2"
                value={form.sprintId} onChange={e=>setField("sprintId", e.target.value)}>
                <option value="">Selecciona sprint</option>
                {sprintOptions.map(s=> <option key={s.id} value={s.id}>{s.name}</option>)}
              </select>
              {errors.sprintId && <p className="text-red-600 text-sm mt-1">{errors.sprintId}</p>}
            </div>
          </div>

          <div>
            <label className="block text-sm font-semibold">Asignado a (Dev)</label>
            <select className="w-full border rounded-xl px-3 py-2"
              value={form.assigneeId} onChange={e=>setField("assigneeId", e.target.value)}>
              <option value="">Selecciona dev</option>
              {devOptions.map(u=> <option key={u.id} value={u.id}>{u.name}</option>)}
            </select>
            {errors.assigneeId && <p className="text-red-600 text-sm mt-1">{errors.assigneeId}</p>}
          </div>

          <div className="flex items-center justify-end gap-2 pt-2">
            <button type="button" onClick={onClose} className="px-4 py-2 rounded-xl border">Cancelar</button>
            <button disabled={submitting} className="px-4 py-2 rounded-xl bg-[#E1261C] text-white">
              {submitting ? "Guardando..." : "Crear tarea"}
            </button>
          </div>

          {errors.estimatedHours && (
            <div className="bg-yellow-50 border border-yellow-200 text-yellow-800 rounded-xl px-3 py-2 text-sm">
              Sugerencia: si necesitas 6–8 horas, crea 2 subtareas de 3–4 h cada una.
            </div>
          )}
        </form>
      </div>
    </div>
  );
}
