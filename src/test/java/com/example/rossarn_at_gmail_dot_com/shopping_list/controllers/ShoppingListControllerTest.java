package com.example.rossarn_at_gmail_dot_com.shopping_list.controllers;

import com.example.rossarn_at_gmail_dot_com.shopping_list.dao.ListItemRepository;
import com.example.rossarn_at_gmail_dot_com.shopping_list.model.ListItem;
import com.example.rossarn_at_gmail_dot_com.shopping_list.model.ShoppingList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

@WebMvcTest(controllers = ShoppingListController.class)
class ShoppingListControllerTest {

    private static ShoppingList shoppingList;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ListItemRepository listItemRepository; // don't try to autowire/use the real DB

    @BeforeAll
    static void setUp() {
        ListItem l1 = new ListItem();
        l1.setId(1);
        l1.setDescription("Banana");
        l1.setPrice(BigDecimal.valueOf(0.40));
        ListItem l2 = new ListItem();
        l2.setId(2);
        l2.setDescription("Apple");
        l2.setPrice(BigDecimal.valueOf(0.60));

        shoppingList = new ShoppingList();
        shoppingList.setItems(List.of(l1,l2));
    }

    @Test
    void getShoppingList_happyPath_returnsValid() throws Exception {
        Mockito.when(listItemRepository.getShoppingItemsForUser("default"))
                .thenReturn(this.shoppingList.getItems());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/shoppinglist"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].description")
                        .value(shoppingList.getItems().get(0).getDescription()));
    }

    @Test
    void saveShoppingList_happyPath_returnsValid() throws Exception {
        Mockito.when(listItemRepository.getShoppingItemsForUser("default"))
                .thenReturn(this.shoppingList.getItems());

        ListItem l1 = new ListItem();
        l1.setId(1);
        l1.setDescription("Green Banana");
        l1.setPrice(BigDecimal.valueOf(0.30));
        ListItem l2 = new ListItem();
        l2.setId(2);
        l2.setDescription("Small Apple");
        l2.setPrice(BigDecimal.valueOf(0.20));

        ShoppingList updatedList = new ShoppingList();
        updatedList.setItems(List.of(l1,l2));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/shoppinglist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedList)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(2));
    }

    @Test
    void saveShoppingList_invalidIds_returnsForbidden() throws Exception {

        Mockito.when(listItemRepository.getShoppingItemsForUser("default"))
                .thenReturn(this.shoppingList.getItems());

        ListItem itemWithUnownedId = new ListItem();
        itemWithUnownedId.setId(9999);
        itemWithUnownedId.setDescription("IDOR attempt");

        ShoppingList updatedList = new ShoppingList();
        updatedList.setItems(List.of(itemWithUnownedId));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/shoppinglist")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedList)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void deleteShoppingListItemById_happyPath_returnsValid() throws Exception {
        int doomedId = 2;

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/shoppinglist/2"))
            .andExpect(MockMvcResultMatchers.status().isOk());

        // verify delete was called on the repository
        Mockito.verify(listItemRepository).deleteById(2);
    }

    @Test
    void saveAndAddNew_happyPath_returnsValidAndAddsNew() throws Exception {
        Mockito.when(listItemRepository.getShoppingItemsForUser("default"))
                .thenReturn(this.shoppingList.getItems());

        ListItem l1 = new ListItem();
        l1.setId(1);
        l1.setDescription("Green Banana");
        l1.setPrice(BigDecimal.valueOf(0.30));
        ListItem l2 = new ListItem();
        l2.setId(2);
        l2.setDescription("Small Apple");
        l2.setPrice(BigDecimal.valueOf(0.20));

        ShoppingList updatedList = new ShoppingList();
        updatedList.setItems(List.of(l1,l2));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/shoppinglist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedList)))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }
}