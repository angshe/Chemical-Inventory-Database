package com.chemical.database;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pubchem")
@CrossOrigin
public class PubChemController {

    private final PubChemService service;

    public PubChemController(PubChemService service) {
        this.service = service;
    }

    @GetMapping("/cid/{cid}")
    public String getByCid(@PathVariable String cid) throws Exception {
        return service.getSdfByCid(cid);
    }

    @GetMapping("/name/{name}")
    public String getByName(@PathVariable String name) throws Exception {
        return service.getSdfByName(name);
    }
}