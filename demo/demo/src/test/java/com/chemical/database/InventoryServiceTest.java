package com.chemical.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Mock
    private ChemicalRepository chemicalRepository;

    @Mock
    private PubChemService pubChemService;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        inventoryService = new InventoryService(chemicalRepository, pubChemService);
    }

    @Test
    void testAddChemical_NewChemical_ParsesSdfData() throws Exception {
        // Arrange
        String identifier = "water";
        double quantity = 100.0;

        String mockSdf = """
                Water
                  -OEChem-01082420122D

                  3  2  0     0  0  0  0  0  0999 V2000
                    2.5369   -0.2500    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
                    2.0000    0.0000    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0
                    1.4631   -0.2500    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0
                  1  2  1  0  0  0  0
                  2  3  1  0  0  0  0
                M  END
                > <PUBCHEM_COMPOUND_CID>
                962

                > <PUBCHEM_MOLECULAR_FORMULA>
                H2O

                > <PUBCHEM_MOLECULAR_WEIGHT>
                18.015

                > <PUBCHEM_MELTING_POINT>
                0 °C

                > <PUBCHEM_BOILING_POINT>
                100 °C

                $$$$
                """;

        when(pubChemService.getSdfByName(identifier)).thenReturn(mockSdf);
        when(chemicalRepository.findByName(identifier)).thenReturn(Optional.empty());
        when(chemicalRepository.save(any(Chemical.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Chemical result = inventoryService.addChemical(identifier, quantity);

        // Assert
        ArgumentCaptor<Chemical> chemicalCaptor = ArgumentCaptor.forClass(Chemical.class);
        verify(chemicalRepository, times(2)).save(chemicalCaptor.capture());

        Chemical savedChemical = chemicalCaptor.getAllValues().get(0);

        assertEquals(identifier, savedChemical.getName());
        assertEquals("H2O", savedChemical.getFormula());
        assertEquals(18.015, savedChemical.getMolarMass());
        assertEquals(0.0, savedChemical.getMeltingPoint());
        assertEquals(100.0, savedChemical.getBoilingPoint());
        assertEquals(100.0, result.getQuantity());
    }

    @Test
    void testAddChemical_ExistingChemical_UpdatesQuantity() throws Exception {
        // Arrange
        String identifier = "water";
        double quantity = 50.0;

        Chemical existingChemical = new Chemical("water", "H2O", 18.015, 0.0, 100.0, 100.0, "962");

        when(chemicalRepository.findByName(identifier)).thenReturn(Optional.of(existingChemical));
        when(chemicalRepository.save(any(Chemical.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Chemical result = inventoryService.addChemical(identifier, quantity);

        // Assert
        assertEquals(150.0, result.getQuantity());
        verify(chemicalRepository, times(1)).save(existingChemical);
    }

    @Test
    void testAddChemical_PubChemFails_StillCreatesChemical() throws Exception {
        // Arrange
        String identifier = "unknown-chemical";
        double quantity = 10.0;

        when(pubChemService.getSdfByName(identifier)).thenThrow(new RuntimeException("PubChem request failed"));
        when(chemicalRepository.findByName(identifier)).thenReturn(Optional.empty());
        when(chemicalRepository.save(any(Chemical.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Chemical result = inventoryService.addChemical(identifier, quantity);

        // Assert
        assertEquals(identifier, result.getName());
        assertNull(result.getFormula());
        assertNull(result.getMolarMass());
        assertEquals(10.0, result.getQuantity());
    }

    @Test
    void testSubtractChemical_Success() {
        // Arrange
        String identifier = "water";
        double quantity = 30.0;

        Chemical existingChemical = new Chemical("water", "H2O", 18.015, 0.0, 100.0, 100.0, "962");

        when(chemicalRepository.findByName(identifier)).thenReturn(Optional.of(existingChemical));
        when(chemicalRepository.save(any(Chemical.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Chemical result = inventoryService.subtractChemical(identifier, quantity);

        // Assert
        assertEquals(70.0, result.getQuantity());
    }

    @Test
    void testSubtractChemical_NotEnoughQuantity_ThrowsException() {
        // Arrange
        String identifier = "water";
        double quantity = 150.0;

        Chemical existingChemical = new Chemical("water", "H2O", 18.015, 0.0, 100.0, 100.0, "962");

        when(chemicalRepository.findByName(identifier)).thenReturn(Optional.of(existingChemical));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.subtractChemical(identifier, quantity);
        });

        assertEquals("Not enough quantity", exception.getMessage());
    }

    @Test
    void testSubtractChemical_ChemicalNotFound_ThrowsException() {
        // Arrange
        String identifier = "nonexistent";
        double quantity = 10.0;

        when(chemicalRepository.findByName(identifier)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.subtractChemical(identifier, quantity);
        });

        assertEquals("Chemical not found", exception.getMessage());
    }
}
