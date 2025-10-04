import { useState } from "react";
import AddTaskDialog from "../components/tasks/AddTaskDialog";

export default function TodoPage() {
  const [dialogOpen, setDialogOpen] = useState(false);
  const [tasks, setTasks] = useState([]); // o tu store global

  const handleCreated = (t) => setTasks(prev => [t, ...prev]);

  return (
    <div className="p-4">
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-extrabold">Tareas</h1>
        <button onClick={()=>setDialogOpen(true)}
          className="px-4 py-2 rounded-2xl bg-[#E1261C] text-white shadow">
          Nueva tarea (Dev)
        </button>
      </div>

      {/* tu board/lista de tareas aquí */}

      <AddTaskDialog
        open={dialogOpen}
        onClose={()=>setDialogOpen(false)}
        onCreated={handleCreated}
        currentUser={{ id: "me" }}
        sprintOptions={[{id:"spr-1", name:"Sprint 1"}]}
        devOptions={[{id:"u1", name:"Claudio"}, {id:"u2", name:"Pedro"}]}
      />
    </div>
  );
}
