import React, { useState, useEffect } from "react";

function ShoppingListTotals({ totalPrice }) {
  const LOCAL_STORAGE_KEY = "budget";

    const [value, setValue] = useState(() => {
      return localStorage.getItem(LOCAL_STORAGE_KEY) || "";
    });

  // TODO: short of time to add this to backend API, so using local storage for now
  useEffect(() => {
    localStorage.setItem(LOCAL_STORAGE_KEY, value);
  }, [value]);

  const budget = parseFloat(value);

  return (
    <div id="budget">
      <span>total</span>
      <span id="total-price">{totalPrice}</span>
      <span>of budget</span>
      <input
        className={totalPrice > budget ? "over-budget" : "in-budget" }
        type="text"
        value={value}
        onChange={(e) => setValue(e.target.value)}
      />
    </div>
  );
}

export default ShoppingListTotals;
