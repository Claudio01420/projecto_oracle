// MtdrSpring/frontend/src/validation/taskSchema.js
export function validateTask(t) {
  const errors = {};
  if (!t.title || t.title.trim().length < 4) errors.title = "Título mínimo 4 caracteres";
  if (t.estimatedHours == null) errors.estimatedHours = "Requerido";
  else if (t.estimatedHours < 0.5) errors.estimatedHours = "Mínimo 0.5 h";
  else if (t.estimatedHours > 4) errors.estimatedHours = "Máximo 4 h. Divide en subtareas";
  if (!t.assigneeId) errors.assigneeId = "Asigna un desarrollador";
  if (!t.status) errors.status = "Selecciona estado";
  if (!t.priority) errors.priority = "Selecciona prioridad";
  if (!t.sprintId) errors.sprintId = "Selecciona sprint";
  return errors;
}
