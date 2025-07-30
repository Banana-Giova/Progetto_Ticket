package it.degroup.it_tickets.presentation.controller;

import it.degroup.it_tickets.entity.Category;
import it.degroup.it_tickets.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    CategoryService service;

    @GetMapping(path = "/categories", produces = "application/json")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<Category> response = service.getAllCategories();
            return ResponseEntity.ok(response);
        } catch (DataAccessException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero delle categorie: " + e.getMessage());
        }
    }
}
