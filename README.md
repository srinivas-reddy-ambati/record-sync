This project simulates a Change Data Capture (CDC) pipeline using Kafka.
The system uses Debezium Kafka Connect to capture database changes from PostgreSQL. When records are inserted, updated, or deleted, Debezium reads the PostgreSQL WAL (Write-Ahead Log) and publishes the change events to a Kafka topic.
A Kafka consumer service subscribes to this topic, processes the events, and invokes a mock external API to replicate the changes in an external system.
All system components are containerized using Docker and orchestrated with Docker Compose.
Architecture Flow
PostgreSQL → Debezium Kafka Connect → Kafka Topic → Kafka Consumer → External Mock API

Limitations
The following non-functional requirements are not implemented in this project:
Rate limiting for external API calls


Idempotency handling to prevent duplicate updates


Retry mechanisms and failure recovery


Dead Letter Queue (DLQ) for failed messages


This project focuses primarily on demonstrating the CDC-based synchronization pipeline.

Instructions to Run the Project
Terminal 1 – Start the System
Navigate to the project directory and start all services.
cd record-sync
docker compose down -v
docker compose build --no-cache
docker compose up
This command will start the following services:
Kafka


PostgreSQL


Debezium Kafka Connect


Topic initialization service


Connector initialization service


External mock API


Kafka consumer


All service logs will appear in Terminal 1.
Once the system is running, any database changes will trigger events that eventually result in API calls to the external mock service, which will also be visible in the logs.

Terminal 2 – Insert or Modify Records in PostgreSQL
Open a new terminal to interact with the database.
Step 1: Find the PostgreSQL container
docker ps
Locate the PostgreSQL container name.
 For example:
record-sync-postgres-1
Step 2: Connect to PostgreSQL
docker exec -it record-sync-postgres-1 psql -U postgres -d inventory
Step 3: Execute SQL Queries
You can simulate database changes using SQL queries.
Example:
INSERT INTO items VALUES ('1','Laptop','Gaming laptop','seller1',5,NOW());
You can also run:
UPDATE items SET quantity = 10 WHERE id = '1';

DELETE FROM items WHERE id = '1';

Observing the Pipeline
After executing SQL commands in Terminal 2, you will see the following flow in Terminal 1 logs:
Debezium captures the database change.


The change event is published to a Kafka topic.


The Kafka consumer reads the event.


The consumer calls the external mock API.


The external API logs the request.


This demonstrates a real-time event-driven synchronization pipeline.
