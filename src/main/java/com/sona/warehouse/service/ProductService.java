package com.sona.warehouse.service;

import com.sona.warehouse.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    public void saveAll(List<Product> products) {
        // TODO: update existing products, add new ones
    }

    public List<Product> findAll() {
        // TODO: implement
        return null;
    }

    synchronized public boolean sell(Long id) {
        // TODO: implement
        return false;
    }
}
