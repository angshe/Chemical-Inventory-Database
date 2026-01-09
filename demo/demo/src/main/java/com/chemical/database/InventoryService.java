package com.chemical.database;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final ChemicalRepository chemicalRepository;
    private final PubChemService pubChemService;

    public InventoryService(ChemicalRepository chemicalRepository, PubChemService pubChemService) {
        this.chemicalRepository = chemicalRepository;
        this.pubChemService = pubChemService;
    }

    private String sdfToName(String sdf) {
        String[] lines = sdf.split("\n");

        for (int x = 0; x < lines.length; x++) {
            if (lines[x].startsWith("> <PUBCHEM_IUPAC_NAME>"))
                return lines[x + 1].trim();
            if (lines[x].startsWith("> <PUBCHEM_OPENEYE_CANONICAL_NAME>"))
                return lines[x + 1].trim();
        }

        return null;
    }

    private String inputToCid(String input) {
        input = input.trim();

        if (input.matches("\\d+")) {
            return input;
        }

        String sdf;
        try {
            sdf = pubChemService.getSdfByName(input);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }

        String[] lines = sdf.split("\n");

        for (int x = 0; x < lines.length; x++) {
            if (lines[x].startsWith("> <PUBCHEM_COMPOUND_CID>"))
                return lines[x + 1].trim();
        }

        return null;
    }

    private String normalizeIdentifier(String input) {
        input = input.trim();

        if (input.matches("\\d+")) {
            try {
                String sdf = pubChemService.getSdfByCid(input);
                input = sdfToName(sdf);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        if (input != null)
            return input.toLowerCase();

        return null;
    }

    public Chemical addChemical(String input, double quantity) {
        String identifier = normalizeIdentifier(input);
        String cid = inputToCid(input);

        try {
            pubChemService.getSdfByName(identifier);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Chemical chemical = chemicalRepository.findByName(identifier)
                .orElseGet(() -> {
                    Chemical c = new Chemical();
                    c.setName(identifier);
                    c.setQuantity(0.0);
                    return chemicalRepository.save(c);
                });

        if (cid != null && (chemical.getCid() == null || chemical.getCid().isEmpty())) {
            chemical.setCid(cid);
        }

        chemical.setQuantity(chemical.getQuantity() + quantity);

        return chemicalRepository.save(chemical);
    }

    public Chemical subtractChemical(String input, double quantity) {

        String identifier = normalizeIdentifier(input);

        try {
            pubChemService.getSdfByName(identifier);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        Chemical chemical = chemicalRepository.findByName(identifier)
                .orElseThrow(() -> new RuntimeException("Chemical not found"));

        if (chemical.getQuantity() < quantity) {
            throw new RuntimeException("Not enough quantity");
        } else {
            chemical.setQuantity(chemical.getQuantity() - quantity);
        }

        return chemicalRepository.save(chemical);
    }

}