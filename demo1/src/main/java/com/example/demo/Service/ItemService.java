package com.example.demo.Service;

import com.example.demo.Entity.Item;
import com.example.demo.Repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public List<Item> findItems(String name) {
        return itemRepository.findByNameContaining(name);
    }

    public void saveItems(List<Item> items) {
    }
}
