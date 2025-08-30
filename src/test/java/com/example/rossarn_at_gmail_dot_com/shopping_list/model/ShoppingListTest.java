package com.example.rossarn_at_gmail_dot_com.shopping_list.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingListTest {

    @Test
    void getItems() {
        ListItem l1 = new ListItem();
        ListItem l2 = new ListItem();

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setItems(List.of(l1,l2));

        assertEquals(2, shoppingList.getItems().size());
    }

    @Test
    void getTotalValue() {
        ListItem l1 = new ListItem();
        l1.setPrice(BigDecimal.valueOf(12.34));
        ListItem l2 = new ListItem();
        l2.setPrice(BigDecimal.valueOf(100));

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setItems(List.of(l1,l2));

        assertEquals(BigDecimal.valueOf(112.34), shoppingList.getTotalValue());
    }
}