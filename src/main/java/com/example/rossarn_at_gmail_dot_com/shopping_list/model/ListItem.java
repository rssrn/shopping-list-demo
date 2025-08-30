package com.example.rossarn_at_gmail_dot_com.shopping_list.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Table(name = "shopping_list_items")
@Entity(name = "ListItems")
public class ListItem {
    @Id
    private int id;

    private String description;

    private Boolean is_marked_off = Boolean.FALSE;

    private int order_index;

    private BigDecimal price = BigDecimal.ZERO;

    private String currency = "GBP";

    private Timestamp created_at;

    private int user_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Boolean getIs_marked_off() {
        return is_marked_off;
    }

    public void setIs_marked_off(Boolean is_marked_off) {
        this.is_marked_off = is_marked_off;
    }

    public int getOrder_index() {
        return order_index;
    }

    public void setOrder_index(int order_index) {
        this.order_index = order_index;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
