package com.sync.api.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @PostMapping
    public String createItem(@RequestBody String body) {

        System.out.println("Received CREATE/UPDATE event from consumer:");
        System.out.println(body);

        return "Item synced successfully";
    }

    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable String id) {

        System.out.println("Received DELETE event from consumer:");
        System.out.println("Item ID: " + id);

        return "Item deleted successfully";
    }
}