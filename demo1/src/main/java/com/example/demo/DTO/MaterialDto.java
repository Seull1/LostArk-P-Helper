package com.example.demo.DTO;

import lombok.Data;

@Data
public class MaterialDto {
    private String name;
    private int quantity;
    private double unitPrice;

    // Getter, Setter, 그리고 생성자

    public MaterialDto(String name, int quantity, double unitPrice) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
