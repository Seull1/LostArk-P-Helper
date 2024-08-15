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
        <h1>오레하 아이템 목록</h1>
        <div v-if="items.length">
          <ul>
            <li v-for="item in items" :key="item.Id" class="item-card">
              <img :src="item.Icon" :alt="item.Name" class="item-icon"/>
              <div class="item-info">
                <p>이름: {{ item.Name }}</p>
                <p>등급: {{ item.Grade }}</p>
                <p>어제 평균 가격: {{ item.YDayAvgPrice }}</p>
                <p>최근 가격: {{ item.RecentPrice }}</p>
                <p>현재 최소 가격: {{ item.CurrentMinPrice }}</p>
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
              <th>레시피</th>
              <th>시세</th>
              <th>제작 비용</th>
              <th>판매 차익</th>
              <th>원가 이익률</th>
              <th>활동력 이익률</th>
              <th>직접 사용</th>
              <th>판매</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="recipe in recipes" :key="recipe.type">
              <td class="recipe-info">
                <img :src="recipe.icon" alt="Recipe Image" class="recipe-icon" />
                <span>{{ recipe.type }} ({{ getMethodLabel(recipe.method) }})</span>
              </td>
              <td>{{ recipe.marketPrice }} 골드</td>
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

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  font-family: Arial, sans-serif;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #2c3e50;
  color: white;
  padding: 10px 20px;
}

.logo {
  font-size: 24px;
  font-weight: bold;
}

.nav ul {
  list-style: none;
  display: flex;
  gap: 20px;
}

.nav ul li a {
  color: white;
  text-decoration: none;
}

.main-content {
  padding: 20px;
  background-color: #ecf0f1;
}

.item-list {
  margin-bottom: 40px;
}

.item-card {
  display: flex;
  align-items: center;
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.item-icon {
  width: 50px;
  height: 50px;
  margin-right: 20px;
  border-radius: 10px;
}

.item-info {
  display: flex;
  flex-direction: column;
}

.item-info p {
  margin: 0;
}

.crafting-calculator {
  margin-bottom: 40px;
}

.recipe-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.recipe-table th,
.recipe-table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.recipe-info {
  display: flex;
  align-items: center;
}

.recipe-icon {
  width: 40px;
  height: 40px;
  margin-right: 10px;
}

.trade-item-prices {
  background-color: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.trade-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.trade-table th,
.trade-table td {
  border: 1px solid #ccc;
  padding: 10px;
  text-align: left;
}

.trade-table th {
  background-color: #f8f8f8;
}

.profit-positive {
  color: green;
}

.profit-negative {
  color: red;
}

</style>
