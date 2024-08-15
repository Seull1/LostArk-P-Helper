methods: {
  calculateTotalCost(recipe) {
    // 제작 비용을 전체 재료 가격과 추가 골드 비용으로 계산
    const materialCost = recipe.materials.reduce((sum, material) => {
      return sum + (material.unitPrice * material.quantity);
    }, 0);
    
    // 제작에 소요되는 총 비용을 반환 (제작 시 생성된 수량만큼 나눔)
    return ((materialCost + recipe.gold) / recipe.quantity).toFixed(2);
  },

  calculateProfit(recipe) {
    // 총 비용을 계산하고, 이를 통해 이익을 계산
    const totalCostPerUnit = parseFloat(this.calculateTotalCost(recipe));
    const marketPricePerUnit = parseFloat(recipe.marketPrice);
    
    // 단위 당 이익 계산
    const profit = marketPricePerUnit - totalCostPerUnit;
    
    // 수익 값을 저장해둠
    recipe.useProfit = profit;
    recipe.sellProfit = profit;

    return profit.toFixed(2);
  },

  calculateCostRate(recipe) {
    // 원가 대비 이익률 계산
    const profit = parseFloat(this.calculateProfit(recipe));
    const totalCost = parseFloat(this.calculateTotalCost(recipe));
    return ((profit / totalCost) * 100).toFixed(2);
  }
}
