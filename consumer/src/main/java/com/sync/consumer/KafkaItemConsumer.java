package com.sync.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaItemConsumer {

    private static final String TOPIC = "inventory-db.public.items";
    private static final String API_URL = "http://record-sync-external-api-1:8081/api/items";

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "inventory-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        AtomicBoolean running = new AtomicBoolean(true);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown signal received...");
            running.set(false);
            consumer.wakeup();
        }));

        ObjectMapper mapper = new ObjectMapper();

        try {

            consumer.subscribe(Collections.singletonList(TOPIC));

            System.out.println("Kafka Consumer Started...");

            while (running.get()) {

                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records) {

                    processRecord(record.value(), mapper);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            consumer.close();
            System.out.println("Kafka Consumer closed");

        }
    }

    private static void processRecord(String value, ObjectMapper mapper) {

        try {

            JsonNode root = mapper.readTree(value);
            JsonNode payload = root.get("payload");

            if (payload == null) return;

            String operation = payload.get("op").asText();

            if (operation.equals("c") || operation.equals("u")) {
                
                System.out.println("Update|Create... " + payload);
                JsonNode after = payload.get("after");
                syncToExternalAPI(after.toString());

            } else if (operation.equals("d")) {
                System.out.println("Delete... " + payload);
                JsonNode before = payload.get("before");
                deleteExternalItem(before.get("item_id").asText());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void syncToExternalAPI(String json) throws Exception {

        URL url = new URL(API_URL);

        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        int response = conn.getResponseCode();

        System.out.println("External Insert|Update API response: " + response);
    }

    private static void deleteExternalItem(String itemId) throws Exception {

        URL url = new URL(API_URL + "/" + itemId);

        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("DELETE");

        int response = conn.getResponseCode();

        System.out.println("External Delete API response: " + response);
    }
}