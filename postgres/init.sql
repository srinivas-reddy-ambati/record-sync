CREATE DATABASE inventory;

\c inventory;

CREATE TABLE items(
 item_id VARCHAR PRIMARY KEY,
 item_name VARCHAR,
 item_desc TEXT,
 seller_id VARCHAR,
 num_of_item INTEGER,
 updated_time TIMESTAMP
);