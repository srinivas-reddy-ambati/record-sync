#!/bin/sh
sleep 25

curl -X POST http://kafka-connect:8083/connectors \
-H "Content-Type: application/json" \
-d '{
  "name": "inventory-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "postgres",
    "database.password": "postgres",
    "database.dbname": "inventory",
    "database.server.name": "inventory-db",
    "table.include.list": "public.items",
    "plugin.name": "pgoutput",
    "topic.prefix": "inventory-db"
  }
}'