package com.example.rossarn_at_gmail_dot_com.shopping_list.controllers;

import com.example.rossarn_at_gmail_dot_com.shopping_list.dao.ListItemRepository;
import com.example.rossarn_at_gmail_dot_com.shopping_list.model.ListItem;
import com.example.rossarn_at_gmail_dot_com.shopping_list.model.ShoppingList;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shoppinglist")
public class ShoppingListController {

    Logger logger = LoggerFactory.getLogger(ShoppingListController.class);

    // bootstrapping with single user for now
    private final int DEFAULT_USER_ID = 1;
    private final String DEFAULT_USER_NAME = "default";

    private ListItemRepository listItemRepository;

    public ShoppingListController(ListItemRepository listItemRepository) {
        this.listItemRepository = listItemRepository;
    }

    @GetMapping
    public ShoppingList getShoppingList(){
        List<ListItem> listItems = listItemRepository.getShoppingItemsForUser(DEFAULT_USER_NAME);
        listItems.forEach(item -> {
            // quirk of the serialiser, json serialisation can fail with null BigDecimals
            if (item.getPrice() == null) {
                item.setPrice(BigDecimal.ZERO);
            }
        });
        return new ShoppingList(listItems);
    }

    @PostMapping
    public ShoppingList saveShoppingList(@RequestBody ShoppingList shoppingList){
        if (!allItemsValidForUser(shoppingList, DEFAULT_USER_NAME)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        saveToRepository(shoppingList);

        // re-fetch all from db
        return getShoppingList();
    }

    @DeleteMapping("/{id}")
    public void deleteShoppingListItemById(@PathVariable Integer id) {
        // TODO also add IDOR protection here, similar to the save operation
        listItemRepository.deleteById(id);
    }

    /**
     *  Convenience endpoint to save any edits and also add one new empty item.
     */
    @PostMapping("/save-add")
    public ShoppingList saveAndAddNew(@RequestBody ShoppingList shoppingList) {

        if (!allItemsValidForUser(shoppingList, DEFAULT_USER_NAME)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        saveToRepository(shoppingList);

        // add new
        ListItem newItem = new ListItem();
        newItem.setDescription("");
        newItem.setUser_id(1);
        // new items should go to the end of the list
        newItem.setOrder_index(Integer.MAX_VALUE);

        listItemRepository.save(newItem);

        // re-fetch all from db
        return getShoppingList();
    }

    /**
     * Prepare shopping list data for saving, and save it
     *
     * @param shoppingList
     */
    private void saveToRepository(ShoppingList shoppingList) {
        int displayOrder = 0;
        for (ListItem item : shoppingList.getItems()) {
            // bootstrap - currently only supporting single-user operation
            item.setUser_id(DEFAULT_USER_ID);

            // prevent XSS - remove html/js from description field
            item.setDescription(Sanitizers.FORMATTING.sanitize(item.getDescription()));

            // preserve user's ordering
            item.setOrder_index(displayOrder++);

            // quirk of the serialiser, json serialisation can fail with null BigDecimals
            if (item.getPrice() == null) {
                item.setPrice(BigDecimal.ZERO);
            }
            listItemRepository.save(item);
        }
    }

    /**
     * Prevent insecure direct object reference attack (IDOR) - prevent malicious user from manually crafting an
     * API request with an ID owned by another user.
     *
     * @param shoppingList the proposed updated shopping list
     * @param username the user making the request
     * @return
     */
    private boolean allItemsValidForUser(ShoppingList shoppingList, String username) {
        List<ListItem> proposedItems = shoppingList.getItems();
        List<ListItem> allowedItems = listItemRepository.getShoppingItemsForUser(username);

        Set<Integer> allowedIds = allowedItems.stream()
                .map(ListItem::getId)
                .collect(Collectors.toSet());

        boolean hasNonPermittedIds = proposedItems.stream()
                .map(ListItem::getId)
                .anyMatch(id -> !allowedIds.contains(id));

        if (hasNonPermittedIds) {
            logger.warn("Possible IDOR attempt: user {} attempted to interact with unowned ID(s)", username);
            return false;
        }

        return true;
    }
}
