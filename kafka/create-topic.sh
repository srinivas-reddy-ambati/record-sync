#!/bin/bash

echo "Waiting for Kafka..."
sleep 20

/opt/kafka/bin/kafka-topics.sh \
--create \
--topic inventory-db.public.items \
--bootstrap-server kafka:9092 \
--partitions 3 \
--replication-factor 1 || true