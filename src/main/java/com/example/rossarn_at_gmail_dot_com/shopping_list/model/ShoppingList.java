package com.example.rossarn_at_gmail_dot_com.shopping_list.model;

import java.math.BigDecimal;
import java.util.List;

public class ShoppingList {
    private List<ListItem> items;

    public ShoppingList() {
    }

    public ShoppingList(List<ListItem> items) {
        this.items = items;
    }

    public List<ListItem> getItems() {
        return items;
    }

    public void setItems(List<ListItem> items) {
        this.items = items;
    }

    public BigDecimal getTotalValue() {
        BigDecimal total = BigDecimal.ZERO;
        for (ListItem item: items) {
            total = total.add(item.getPrice());
        }
        return total;
    }
}
