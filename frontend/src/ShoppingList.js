import React, { useState, useEffect } from "react";



function ShoppingList() {
  const [items, setItems] = useState([]);
  const [error, setError] = useState(null);

  // Fetch items from the Spring API
  useEffect(() => {
    fetch("/api/shoppinglist")
      .then((response) => {
        if (!response.ok) throw new Error("Network response was not ok");
        return response.json();
      })
      .then((data) => {
        setItems(data.items);
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

    // validation for the price field
    if (fieldname === "price") {
      const priceRegex = /^(?:\d+)(?:\.\d{0,2})?$/;
      if (!priceRegex.test(value)) {
        setError("price fields must be decimal currency, max 2 decimal places")
      } else {
        setError(null);
      }
    }
  };

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

  return (
    <div id="shopping-list">
      {error && <div className="error-message">{error}</div>}
      <ul>
        {items.map((item) => (
        <li key={item.id}>
            <input
              type="text"
              className="desc"
              value={item.description || ""}
              onChange={(e) => handleChange(item.id, "description", e.target.value)}
            />
            <input
              size="10"
              type="text"
              className="price"
              value={item.price || ""}
              onChange={(e) => handleChange(item.id, "price", e.target.value)}
            />
            <input
              type="checkbox"
              checked={item.is_marked_off}
              onChange={(e) => handleChange(item.id, "is_marked_off", e.target.checked)}
            />
            <button className="remove-item" onClick={(e) => handleRemove(item.id)}>Remove</button>
          </li>
        ))}
      </ul>
      <div className="buttons">
        <button id="add-new" onClick={handleAdd}>New Item</button>
        <button id="save" onClick={handleSave}>Save</button>
      </div>
    </div>
  );
}

export default ShoppingList;