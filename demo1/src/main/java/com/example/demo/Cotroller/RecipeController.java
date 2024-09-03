package com.example.demo.Cotroller;

import com.example.demo.DTO.RecipeDto;
import com.example.demo.Service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping
    public List<RecipeDto> getRecipes() {
        return recipeService.getAllRecipes();
    }
}