package com.example.demo.Service;

import com.example.demo.DTO.MaterialDto;
import com.example.demo.DTO.RecipeDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {

    public List<RecipeDto> getAllRecipes() {
        List<RecipeDto> recipes = new ArrayList<>();

        // 예시 레시피 데이터 추가
        RecipeDto recipe = new RecipeDto(
                "오레하 융화 재료",
                "수렵",
                "https://cdn-lostark.game.onstove.com/efui_iconatlas/use/use_9_71.png",
                205, // 기본 제작 비용 (골드)
                30, // 생성되는 수량
                216, // 소모 활동력
                List.of(
                        new MaterialDto("오레하 두툼한 생고기", 10, 20),
                        new MaterialDto("다듬은 생고기", 40, 5),
                        new MaterialDto("두툼한 생고기", 80, 2)
                )
        );

        // 각 레시피별로 계산 수행
        calculateRecipeData(recipe);

        recipes.add(recipe);

        return recipes;
    }

    private void calculateRecipeData(RecipeDto recipe) {
        // 재료 총 비용 계산
        double materialCost = recipe.getMaterials().stream()
                .mapToDouble(material -> material.getQuantity() * material.getUnitPrice())
                .sum();

        // 전체 제작 비용 계산
        double totalCost = (materialCost + recipe.getTotalCost()) / recipe.getQuantity();
        recipe.setTotalCost(totalCost);

        // 시장 가격에 따른 이익 계산
        double marketPrice = recipe.getMarketPrice();
        double fee = Math.max(marketPrice * 0.05, 1); // 수수료 계산 (5%, 최소 1골드)
        double profit = marketPrice - fee - totalCost;
        recipe.setProfit(profit);

        // 원가 이익률 계산
        double costRate = (profit / totalCost) * 100;
        recipe.setCostRate(costRate);

        // 활동력 이익률 계산
        double energyRate = (profit / recipe.getEnergy()) * 100;
        recipe.setEnergyRate(energyRate);
    }
}
