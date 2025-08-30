CREATE DATABASE IF NOT EXISTS shoppinglist;

USE shoppinglist;

CREATE TABLE users (
    id             INT AUTO_INCREMENT      PRIMARY KEY,
    username       VARCHAR(50)             NOT NULL UNIQUE,
    email          VARCHAR(100)            NOT NULL UNIQUE,
    password_hash  VARCHAR(255)            NOT NULL,
    spending_limit DECIMAL(10,2),
    created_at     TIMESTAMP               DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE shopping_list_items (
    id            INT AUTO_INCREMENT       PRIMARY KEY,
    user_id       INT                      NOT NULL,
    description   TEXT                     NOT NULL,
    is_marked_off BOOLEAN                  NOT NULL DEFAULT FALSE,
    order_index   INT                      NOT NULL DEFAULT 0,
    price         DECIMAL(10,2),
    currency      CHAR(3)                  NOT NULL DEFAULT 'GBP',
    created_at    TIMESTAMP                DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- create dummy user for prototyping as a single-user system
insert into users(username, email, password_hash) values ("default", "test@example.com", "xxx");