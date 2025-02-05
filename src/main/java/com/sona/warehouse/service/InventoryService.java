package com.sona.warehouse.service;

import com.sona.warehouse.model.Inventory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {
    public void saveAll(List<Inventory> inventories) {
        // TODO: consider existing articles should be added, not created
    }
}
