package com.example.demo.DTO;

import lombok.Data;

import java.util.List;

@Data
public class RecipeDto {
    private String type;
    private String icon;
    private String method;
    private double marketPrice;
    private double totalCost;
    private double profit;
    private double costRate;
    private double energyRate;
    private int energy;
    private int quantity;
    private List<MaterialDto> materials;

    // Getter, Setter, 그리고 생성자

    public RecipeDto(String type, String method, String icon, double gold, int quantity, int energy, List<MaterialDto> materials) {
        this.type = type;
        this.method = method;
        this.icon = icon;
        this.totalCost = gold;
        this.quantity = quantity;
        this.energy = energy;
        this.materials = materials;
    }
}
