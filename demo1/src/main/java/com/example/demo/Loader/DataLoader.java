package com.example.demo.Loader;

import com.example.demo.Entity.Item;
import com.example.demo.Service.ItemService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader {

    @Autowired
    private ItemService itemService;

    @PostConstruct
    public void loadData() {
        // Sample data; replace with actual API call
        List<Item> items = new ArrayList<>();
        items.add(new Item("Item1", "Legendary", 123.45));
        items.add(new Item("Item2", "Rare", 67.89));
        // Add more items...

        itemService.saveItems(items);
    }
}
