<template>
  <div class="app-container">
    <!-- 헤더 -->
    <header class="header">
      <div class="logo">LostArk Marketplace</div>
      <nav class="nav">
        <ul>
          <li><a href="#">Home</a></li>
          <li><a href="#">Items</a></li>
          <li><a href="#">Crafting</a></li>
          <li><a href="#">Market Prices</a></li>
        </ul>
      </nav>
    </header>

    <!-- 메인 콘텐츠 -->
    <main class="main-content">
      <!-- 오레하 아이템 목록 -->
      <section class="item-list">
        <!-- <h1>오레하 아이템 목록</h1> -->
        <div v-if="items.length">
          <ul>
            <li v-for="item in items" :key="item.Id" class="item-card">
              <img :src="item.Icon" :alt="item.Name" class="item-icon"/>
              <div class="item-info">
                <p>현재 최소 가격: {{ item.CurrentMinPrice }}<span style="color: #FFD700" >G</span></p>
                <p>최근 거래량: {{ item.TradeCount || 'N/A' }}</p>
              </div>
            </li>
          </ul>
        </div>
        <p v-else>오레하 관련 데이터가 없습니다.</p>
      </section>

      <!-- 오레하 제작 비용 계산기 -->
      <section class="crafting-calculator">
        <h2>오레하 제작 비용 계산기</h2>
        <table class="recipe-table">
          <thead>
            <tr>
              <th @click="sortTable('type')">레시피</th>
              <th @click="sortTable('marketPrice')">시세</th>
              <th @click="sortTable('totalCost')">제작 비용</th>
              <th @click="sortTable('unitCost')">개당 제작 비용</th>
              <th @click="sortTable('profit')">판매 차익</th>
              <th @click="sortTable('costRate')">원가 이익률</th>
              <th @click="sortTable('energyRate')">활동력 이익률</th>
              <th @click="sortTable('useProfit')">직접 사용</th>
              <th @click="sortTable('sellProfit')">판매</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="recipe in recipes" :key="recipe.type">
              <td class="recipe-info">
                <img :src="recipe.icon" alt="Recipe Image" class="recipe-icon" />
                <span>{{ recipe.type }} ({{ getMethodLabel(recipe.method) }})</span>
              </td>
              <td>{{ recipe.marketPrice }} 골드</td>
              <td>{{ calculateTotalCost(recipe) }} 골드</td>
              <td>{{ (calculateTotalCost(recipe) / recipe.quantity).toFixed(2)}} 골드</td>
              <td>{{ (calculateProfit(recipe) / recipe.quantity).toFixed(2) }} 골드</td>
              <td>{{ calculateCostRate(recipe) }}%</td>
              <td>{{ calculateEnergyRate(recipe) }}%</td>
              <td :class="getProfitClass(recipe, 'use')">{{ recipe.useProfit > 0 ? '이득' : '손해' }}</td>
              <td :class="getProfitClass(recipe, 'sell')">{{ recipe.sellProfit > 0 ? '이득' : '손해' }}</td>
            </tr>
          </tbody>
        </table>
      </section>

      <!-- 생활 전리품 경매장 가격
      <section class="trade-item-prices">
        <h2>생활 전리품 경매장 가격</h2>
        <table v-if="tradeItems.length" class="trade-table">
          <thead>
            <tr>
              <th>아이템</th>
              <th>최소 가격 (골드)</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in tradeItems" :key="item.Id">
              <td>{{ item.Name }}</td>
              <td>{{ item.CurrentMinPrice !== undefined ? item.CurrentMinPrice : '정보 없음' }}</td>
            </tr>
          </tbody>
        </table>
        <p v-else>생활 전리품 데이터가 없습니다.</p>
      </section> -->
    </main>
  </div>
</template>

<script>
import axiosInstance from '../axiosInstance';

export default {
  name: 'LostArkMarketplace',
  data() {
    return {
      items: [],
      tradeItems: [],
      recipes: [
        {
          type: '오레하 융화 재료',
          icon: 'https://cdn-lostark.game.onstove.com/efui_iconatlas/use/use_9_71.png', // 이미지 URL 추가
          method: '수렵',
          time: '00:45:00',
          energy: 216,
          gold: 205,
          quantity: 30,
          materials: [
            { name: '오레하 두툼한 생고기', quantity: 10, unitPrice: 0 },
            { name: '다듬은 생고기', quantity: 40, unitPrice: 0 },
            { name: '두툼한 생고기', quantity: 80, unitPrice: 0 }
          ]
        },
        {
          type: '상급 오레하 융화 재료',
          icon: 'https://cdn-lostark.game.onstove.com/efui_iconatlas/use/use_8_109.png',
          method: '수렵',
          time: '01:00:00',
          energy: 288,
          gold: 250,
          quantity: 20,
          materials: [
            { name: '오레하 두툼한 생고기', quantity: 16, unitPrice: 0 },
            { name: '다듬은 생고기', quantity: 64, unitPrice: 0 },
            { name: '두툼한 생고기', quantity: 128, unitPrice: 0 }
          ]
        },
        {
          type: '최상급 오레하 융화 재료',
          icon: 'https://cdn-lostark.game.onstove.com/efui_iconatlas/use/use_11_29.png',
          method: '수렵',
          time: '01:15:00',
          energy: 360,
          gold: 300,
          quantity: 15,
          materials: [
            { name: '오레하 두툼한 생고기', quantity: 52, unitPrice: 0 },
            { name: '다듬은 생고기', quantity: 69, unitPrice: 0 },
            { name: '두툼한 생고기', quantity: 142, unitPrice: 0 }
          ]
        },
        {
          type: '오레하 융화 재료',
          icon: 'https://cdn-lostark.game.onstove.com/efui_iconatlas/use/use_9_71.png',
          method: '낚시',
          time: '00:45:00',
          energy: 216,
          gold: 80,
          quantity: 30,
          materials: [
            { name: '오레하 태양 잉어', quantity: 10, unitPrice: 0 },
            { name: '붉은 살 생선', quantity: 40, unitPrice: 0 },
            { name: '생선', quantity: 80, unitPrice: 0 }
          ]
        },
        {
          type: '상급 오레하 융화 재료',
          icon: 'https://cdn-lostark.game.onstove.com/efui_iconatlas/use/use_8_109.png',
          method: '낚시',
          time: '01:00:00',
          energy: 288,
          gold: 250,
          quantity: 20,
          materials: [
            { name: '오레하 태양 잉어', quantity: 16, unitPrice: 0 },
            { name: '붉은 살 생선', quantity: 64, unitPrice: 0 },
            { name: '생선', quantity: 128, unitPrice: 0 }
          ]
        },
        {
          type: '최상급 오레하 융화 재료',
          icon: 'https://cdn-lostark.game.onstove.com/efui_iconatlas/use/use_11_29.png',
          method: '낚시',
          time: '01:15:00',
          energy: 360,
          gold: 300,
          quantity: 15,
          materials: [
            { name: '오레하 태양 잉어', quantity: 52, unitPrice: 0 },
            { name: '붉은 살 생선', quantity: 69, unitPrice: 0 },
            { name: '생선', quantity: 142, unitPrice: 0 }
          ]
        },
        {
          type: '오레하 융화 재료',
          icon: 'https://cdn-lostark.game.onstove.com/efui_iconatlas/use/use_9_71.png',
          method: '고고학',
          time: '00:45:00',
          energy: 216,
          gold: 205,
          quantity: 30,
          materials: [
            { name: '오레하 유물', quantity: 8, unitPrice: 0 },
            { name: '희귀한 유물', quantity: 26, unitPrice: 0 },
            { name: '고대 유물', quantity: 64, unitPrice: 0 }
          ]
        },
        {
          type: '상급 오레하 융화 재료',
          icon: 'https://cdn-lostark.game.onstove.com/efui_iconatlas/use/use_8_109.png',
          method: '고고학',
          time: '01:00:00',
          energy: 288,
          gold: 250,
          quantity: 20,
          materials: [
            { name: '오레하 유물', quantity: 16, unitPrice: 0 },
            { name: '희귀한 유물', quantity: 29, unitPrice: 0 },
            { name: '고대 유물', quantity: 94, unitPrice: 0 }
          ]
        },
        {
          type: '최상급 오레하 융화 재료',
          icon: 'https://cdn-lostark.game.onstove.com/efui_iconatlas/use/use_11_29.png',
          method: '고고학',
          time: '01:15:00',
          energy: 360,
          gold: 300,
          quantity: 15,
          materials: [
            { name: '오레하 유물', quantity: 52, unitPrice: 0 },
            { name: '희귀한 유물', quantity: 51, unitPrice: 0 },
            { name: '고대 유물', quantity: 107, unitPrice: 0 }
          ]
        }
      ]
    };
  },
  async mounted() {
    await this.fetchItems();
    await this.fetchTradeItems();
    this.calculateMaterialPrices();
  },
  

  methods: {
  async fetchItems() {
    try {
      const response = await axiosInstance.post('/markets/items', {
        Sort: 'GRADE',
        CategoryCode: 50010,
        ItemName: '오레하',
        PageNo: 1,
        SortCondition: 'ASC',
      });

      const items = response.data.Items;
      for (const item of items) {
        const detailsResponse = await axiosInstance.get(`/markets/items/${item.Id}`);
        const details = detailsResponse.data[0];
        item.TradeCount = details.Stats[0]?.TradeCount || 'N/A';
        item.YDayAvgPrice = details.YDayAvgPrice;
        item.RecentPrice = details.RecentPrice;

        // 각 아이템의 marketPrice를 레시피에 매핑 (이름과 method를 함께 기준으로 함)
        this.recipes.forEach(recipe => {
          if (recipe.type.includes(item.Name)) {
            recipe.marketPrice = item.CurrentMinPrice || 0;
          }
        });
      }

      this.items = items;
    } catch (error) {
      console.error('Error fetching items:', error);
    }
  },

  async fetchTradeItems() {
    try {
      const tradeCodes = [90500, 90600, 90700]; // 수렵, 낚시, 고고학 전리품 코드
      const requests = tradeCodes.map(code =>
        axiosInstance.post('/markets/items', {
          CategoryCode: code,
          Sort: 'GRADE',
          PageNo: 1,
          SortCondition: 'ASC',
        })
      );

      const responses = await Promise.all(requests);
      this.tradeItems = responses.flatMap(response => response.data.Items || []);
    } catch (error) {
      console.error('Error fetching trade items:', error);
    }
  },

  calculateMaterialPrices() {
    this.recipes.forEach(recipe => {
      recipe.materials.forEach(material => {
        const tradeItem = this.tradeItems.find(item => item.Name === material.name);
        if (tradeItem && tradeItem.CurrentMinPrice !== undefined) {
          material.unitPrice = tradeItem.CurrentMinPrice / 100; // 100개 단위로 나누어 단가 계산
        } else {
          material.unitPrice = 0; // 값이 없을 때 기본값을 0으로 설정
        }
      });
    });
  },

  calculateTotalCost(recipe) {
    const materialCost = recipe.materials.reduce((sum, material) => {
      return sum + (material.unitPrice * material.quantity);
    }, 0);
    return ((materialCost + recipe.gold) / recipe.quantity).toFixed(2);
  },

  calculateProfit(recipe) {
    const totalCostPerUnit = parseFloat(this.calculateTotalCost(recipe));
    const marketPricePerUnit = parseFloat(recipe.marketPrice);

    const profit = marketPricePerUnit - totalCostPerUnit;
    recipe.useProfit = profit;
    recipe.sellProfit = profit;

    return profit.toFixed(2);
  },

  calculateCostRate(recipe) {
    const profit = parseFloat(this.calculateProfit(recipe));
    const totalCost = parseFloat(this.calculateTotalCost(recipe));
    return ((profit / totalCost) * 100).toFixed(2);
  },

  calculateEnergyRate(recipe) {
    const profit = parseFloat(this.calculateProfit(recipe));
    return ((profit / recipe.energy) * 100).toFixed(2);
  },

  getMethodLabel(method) {
    const methods = {
      '수렵': '수렵',
      '낚시': '낚시',
      '고고학': '고고학',
    };
    return methods[method];
  },

  getProfitClass(recipe, type) {
    const profit = type === 'use' ? recipe.useProfit : recipe.sellProfit;
    return profit > 0 ? 'profit-positive' : 'profit-negative';
  }
}





};
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Roboto', sans-serif;
  background-color: #f5f5f5;
  color: #34495e;
}

.app-container {
  display: flex;
  flex-direction: column;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #273c75;
  color: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 26px;
  font-weight: 700;
  letter-spacing: 1px;
}

.nav ul {
  list-style: none;
  display: flex;
  gap: 20px;
}

.nav ul li a {
  color: white;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.nav ul li a:hover {
  color: #fbc531;
}

.main-content {
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
}




.crafting-calculator {
  margin-bottom: 40px;
}

.crafting-calculator h2 {
  margin-bottom: 20px;
  color: #2c3e50;
  font-size: 22px;
}

.recipe-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.recipe-table th,
.recipe-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #dfe6e9;
  font-weight: 500;
}

.recipe-table th {
  background-color: #273c75;
  color: white;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
}

.recipe-table td {
  background-color: white;
}

.recipe-info {
  display: flex;
  align-items: center;
}

.recipe-icon {
  width: 40px;
  height: 40px;
  margin-right: 10px;
  border-radius: 8px;
  object-fit: cover;
}

.profit-positive {
  color: #27ae60;
  font-weight: 700;
}

.profit-negative {
  color: #e74c3c;
  font-weight: 700;
}

/* 호버 시 배경 강조 */
.recipe-table tr:hover td {
  background-color: #f5f6fa;
}

/* 전체 레이아웃 */
.trade-item-prices {
  background-color: #ffffff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.trade-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.trade-table th,
.trade-table td {
  border: 1px solid #dfe6e9;
  padding: 12px 15px;
  text-align: left;
}


.item-list {
  margin: 20px 0;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.item-list h1 {
  font-size: 24px;
  margin-bottom: 20px;
  text-align: center;
  color: #333;
}

.item-card {
  display: inline-flex;
  align-items: center;
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 8px 12px;
  margin: 8px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  white-space: nowrap;
}

.item-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.15);
}

.item-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  margin-right: 12px;
  object-fit: cover;
}

.item-info {
  display: flex;
  flex-direction: column;
  font-size: 14px;
}

.item-info p {
  margin: 2px 0;
  font-size: 14px;
  color: #555;
}

.item-info p:first-child {
  font-weight: bold;
  color: #333;
}

.item-info p:last-child {
  color: #007bff;
}


</style>