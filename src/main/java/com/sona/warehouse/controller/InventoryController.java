package com.sona.warehouse.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sona.warehouse.dto.InventoryDTO;
import com.sona.warehouse.exceptions.CustomHttpStatusCodeException;
import com.sona.warehouse.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller for handling inventory-related operations.
 * Provides an endpoint for uploading inventory data via a JSON file.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    /**
     * Constructor for InventoryController.
     *
     * @param inventoryService The service handling inventory operations.
     * @param objectMapper     The Jackson ObjectMapper for JSON processing.
     */
    @Autowired
    public InventoryController(InventoryService inventoryService, ObjectMapper objectMapper) {
        this.inventoryService = inventoryService;
        this.objectMapper = objectMapper;
    }

    /**
     * Uploads and processes an inventory JSON file.
     * Reads the file content, converts it into an {@link InventoryDTO}, and saves it to the database.
     *
     * @param file The uploaded JSON file containing inventory data.
     * @return ResponseEntity with a success or error message.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadInventory(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty or not provided.");
        }

        try {
            InventoryDTO inventory = objectMapper.readValue(file.getBytes(), objectMapper.getTypeFactory().constructType(InventoryDTO.class));

            inventoryService.saveAll(inventory);
            return ResponseEntity.ok("Inventory uploaded successfully!");

        }catch (CustomHttpStatusCodeException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
        } catch (JsonMappingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format: " + e.getMessage());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing JSON: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload inventory file: " + e.getMessage());
        }
    }
}
