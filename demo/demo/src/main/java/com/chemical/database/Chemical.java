package com.chemical.database;

import jakarta.persistence.*;

@Entity
public class Chemical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String formula;
    private Double molarMass;
    private Double meltingPoint;
    private Double boilingPoint;
    private Double quantity;
    private String cid;

    public Chemical() {
    }

    public Chemical(String name, String formula, Double molarMass, Double meltingPoint, Double boilingPoint,
            Double quantity, String cid) {
        this.name = name;
        this.formula = formula;
        this.molarMass = molarMass;
        this.meltingPoint = meltingPoint;
        this.boilingPoint = boilingPoint;
        this.quantity = quantity;
        this.cid = cid;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCid() {
        return cid;
    }

    public String getFormula() {
        return formula;
    }

    public Double getMolarMass() {
        return molarMass;
    }

    public Double getMeltingPoint() {
        return meltingPoint;
    }

    public Double getBoilingPoint() {
        return boilingPoint;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public void setMolarMass(Double molarMass) {
        this.molarMass = molarMass;
    }

    public void setMeltingPoint(Double meltingPoint) {
        this.meltingPoint = meltingPoint;
    }

    public void setBoilingPoint(Double boilingPoint) {
        this.boilingPoint = boilingPoint;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}