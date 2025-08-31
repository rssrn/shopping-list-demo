import React, { memo } from "react";
import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";

function ShoppingListItem({ item, onChange, onRemove }) {
  const { attributes, listeners, setNodeRef, transform, transition } = useSortable({ id: item.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    display: "flex",
    alignItems: "center",
    gap: "0.5rem",
  };

  return (
    <li ref={setNodeRef} style={style}>
      <span {...listeners} {...attributes} className="drag-handle" style={{ cursor: "grab" }}>
        ☰
      </span>
      <input
        type="text"
        className="desc"
        value={item.description || ""}
        onChange={(e) => onChange(item.id, "description", e.target.value)}
      />
      <input
        type="text"
        className="price"
        value={item.price || ""}
        onChange={(e) => onChange(item.id, "price", e.target.value)}
      />
      <input
        type="checkbox"
        checked={item.is_marked_off || false}
        onChange={(e) => onChange(item.id, "is_marked_off", e.target.checked)}
      />
      <button className="remove-item" onClick={() => onRemove(item.id)}>Remove</button>
    </li>
  );
}

export default ShoppingListItem;
