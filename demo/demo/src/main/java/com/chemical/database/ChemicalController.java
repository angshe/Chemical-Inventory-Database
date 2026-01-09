package com.chemical.database;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/molecules")
public class ChemicalController {

    private final InventoryService inventoryService;
    private final ChemicalRepository repository;

    public ChemicalController(InventoryService inventoryService, ChemicalRepository repository) {
        this.inventoryService = inventoryService;
        this.repository = repository;
    }

    @PostMapping("/add")
    public Chemical addChemical(
            @RequestParam String identifier,
            @RequestParam double quantity
    ) throws Exception {
        return inventoryService.addChemical(identifier, quantity);
    }

    @PostMapping("/subtract")
    public Chemical subtractChemical(
            @RequestParam String identifier,
            @RequestParam double quantity
    ) throws Exception {
        return inventoryService.subtractChemical(identifier, quantity);
    }

    @GetMapping("/all")
    public List<Chemical> getAllChemicals() {
        return repository.findAll();
    }

}