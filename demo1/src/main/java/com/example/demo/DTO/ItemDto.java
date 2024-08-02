package com.example.demo.DTO;

public class ItemDto {
    private Long id;
    private String name;
    private String grade;
    // getters and setters
}

public class ItemDetailDto {
    private Long id;
    private String name;
    private String description;
    private List<MaterialDto> materials;
    // getters and setters
}

public class MaterialDto {
    private Long id;
    private String name;
    private Integer amount;
    private Double price;
    // getters and setters
}