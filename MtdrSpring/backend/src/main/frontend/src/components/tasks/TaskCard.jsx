// project: projecto_oracle/MtdrSpring/frontend/src/components/tasks/TaskCard.jsx
import React from "react";

export default function TaskCard({ task, onClick }) {
  const { title, description, estimatedHours, assigneeId, priority, status } = task;

  const priorityColors = {
    low: "bg-green-100 text-green-700",
    medium: "bg-yellow-100 text-yellow-700",
    high: "bg-red-100 text-red-700",
  };

  return (
    <div
      onClick={onClick}
      className="p-4 bg-white rounded-2xl shadow-sm border border-gray-200 hover:shadow-md transition-all cursor-pointer mb-2"
    >
      <div className="flex items-center justify-between mb-2">
        <h3 className="font-bold text-gray-900 text-base truncate">{title}</h3>
        <span
          className={`text-xs font-semibold px-2 py-0.5 rounded-lg ${priorityColors[priority]}`}
        >
          {priority.toUpperCase()}
        </span>
      </div>

      {description && (
        <p className="text-gray-600 text-sm mb-2 line-clamp-2">{description}</p>
      )}

      <div className="flex items-center justify-between text-xs text-gray-500 mt-1">
        <span>⏱ {estimatedHours}h</span>
        <span>👤 {assigneeId}</span>
        <span
          className={`font-semibold ${
            status === "done"
              ? "text-green-600"
              : status === "in_progress"
              ? "text-blue-600"
              : "text-gray-400"
          }`}
        >
          {status.replace("_", " ")}
        </span>
      </div>
    </div>
  );
}
