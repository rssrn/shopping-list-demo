package com.example.rossarn_at_gmail_dot_com.shopping_list.dao;

import com.example.rossarn_at_gmail_dot_com.shopping_list.model.ListItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListItemRepository extends CrudRepository<ListItem, Integer> {


    // @Query("select l from shopping_list_items l, users u where u.user_id = l.user_id and u.username = "default_user");
    @Query("select l from ListItems l where user_id = 1")
    public List<ListItem> getShoppingItemsForUser(@Param("username") String username);


}
