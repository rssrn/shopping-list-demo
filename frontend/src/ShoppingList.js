import React, { useState, useEffect } from "react";

import ShoppingListItem from './ShoppingListItem.js';
import ShoppingListTotals from './ShoppingListTotals';

import {
  DndContext,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
} from "@dnd-kit/core";
import {
  arrayMove,
  SortableContext,
  useSortable,
  verticalListSortingStrategy,
} from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";

function ShoppingList() {
  const [items, setItems] = useState([]);
  const [error, setError] = useState(null);
  const [totalPrice, setTotalPrice] = useState(0);

  // Fetch items from the Spring API
  useEffect(() => {
    fetch("/api/shoppinglist")
      .then((response) => {
        if (!response.ok) throw new Error("Network response was not ok");
        return response.json();
      })
      .then((data) => {
        setItems(data.items);
        updateTotalPrice(data.items);
      })
      .catch((error) => {
        console.error("Error fetching items:", error);
        setError("Failed to load shopping list");
      });
  }, []);


 const handleChange = (id, fieldname, value) => {
    const updated = items.map((item) =>
      item.id === id ? { ...item, [fieldname]: value } : item
    );
    setItems(updated);

    if (fieldname === "price") {
      // validation
      const priceRegex = /^(?:\d*)(?:\.\d{0,2})?$/;
      if (!priceRegex.test(value)) {
        setError("price fields must be decimal currency, max 2 decimal places")
      } else {
        setError(null);
      }

      updateTotalPrice(updated);

    }

  };

  const updateTotalPrice = (latestItems) => {
        let sum = 0;
        for (const item of latestItems) {
          const parsed = parseFloat(item.price);
          if (!isNaN(parsed)) {
            sum += parsed;
          }
        }
        setTotalPrice(sum);
  }

  const handleRemove = (id) => {
    setError(null);
    fetch("/api/shoppinglist/" + id, {
      method: "DELETE"
    })
      .then((response) => {
        if (response.ok) {
            setItems(items.filter(item => item.id !== id));
          } else {
            throw new Error("Failed to delete");
          }
      })
      .catch((error) => {
        console.error("Error removing item:", error);
        setError("Failed to remove item")
      });
  }

  const handleSave = () => {
    setError(null);

    fetch("/api/shoppinglist", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ items }),
    })
      .then((response) => {
        if (!response.ok) throw new Error("Failed to save");
        return response.json();
      })
      .then((data) => {
        setItems(data.items); // refresh with saved data
      })
      .catch((error) => {
        console.error("Error saving shopping list:", error);
        setError("Failed to save shopping list.")
      });
  };

  const handleAdd = () => {
    setError(null);

      fetch("/api/shoppinglist/save-add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ items }),
      })
        .then((response) => {
          if (!response.ok) throw new Error("Failed to add");
          return response.json();
        })
        .then((data) => {
          setItems(data.items); // refresh with saved data
        })
        .catch((error) => {
          console.error("Error adding shopping list item:", error);
          setError("Failed to add")
        });
  }

  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 5 } })
  );

  const handleDragEnd = (event) => {
    const { active, over } = event;
    if (active.id !== over.id) {
      const oldIndex = items.findIndex((item) => item.id === active.id);
      const newIndex = items.findIndex((item) => item.id === over.id);
      setItems((items) => arrayMove(items, oldIndex, newIndex));
    }
  };

  function SortableItem({ id, children }) {
    const {
      attributes,
      listeners,
      setNodeRef,
      transform,
      transition,
    } = useSortable({ id });

    const style = {
      transform: CSS.Transform.toString(transform),
      transition,
      cursor: "grab",
    };

    return (
      <li ref={setNodeRef} style={style} {...attributes}>
        <span {...listeners} className="drag-handle">☰</span>
        {children}
      </li>
    );
  }

  return (
    <div id="shopping-list">
      <div className="error-message">{error || ""}</div>
      <DndContext
        sensors={sensors}
        collisionDetection={closestCenter}
        onDragEnd={handleDragEnd}
      >
        <SortableContext
          items={items.map((item) => item.id)}
          strategy={verticalListSortingStrategy}
        >
          <ul>
            {items.map((item) => (
            <ShoppingListItem
              key={item.id}
              item={item}
              onChange={handleChange}
              onRemove={handleRemove}
             />
             ))}
          </ul>
        </SortableContext>
      </DndContext>

      <ShoppingListTotals totalPrice={totalPrice}/>

      <div className="buttons">
        <button id="add-new" onClick={handleAdd}>New Item</button>
        <button id="save" onClick={handleSave}>Save</button>
      </div>
    </div>
  );
}

export default ShoppingList;