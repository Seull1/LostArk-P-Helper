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
        <div class="crafting-grid">
          <div v-for="(recipe, index) in recipes" :key="index" class="crafting-card">
          <img :src="recipe.Icon" :alt="recipe.Name" class="item-icon"/>
            <div class="crafting-info">
              <h3>{{ recipe.type }} <span class="method">{{ getMethodLabel(recipe.method) }}</span></h3>
              <p>소요 시간: {{ recipe.time }}</p>
              <p>필요 활동력: {{ recipe.energy }} 활동력</p>
              <ul>
                <li v-for="material in recipe.materials" :key="material.name">
                  {{ material.name }}: {{ material.quantity }}개 (단가: {{ material.unitPrice || '정보 없음' }} 골드)
                </li>
              </ul>
              <p>골드: {{ recipe.gold }} 골드</p>
              <p>1개 제작 비용: {{ calculateUnitCost(recipe) }} 골드</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 생활 전리품 경매장 가격 -->
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
      </section>
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
          method: '고고학',
          time: '01:00:00',
          energy: 288,
          gold: 250,
          quantity: 20,
          materials: [
            { name: '오레하 유물', quanti연ty: 16, unitPrice: 0 },
            { name: '희귀한 유물', quantity: 29, unitPrice: 0 },
            { name: '고대 유물', quantity: 94, unitPrice: 0 }
          ]
        },
        {
          type: '최상급 오레하 융화 재료',
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
            material.unitPrice = tradeItem.CurrentMinPrice;
          } else {
            console.warn(`Item not found or price unavailable for: ${material.name}`);
            material.unitPrice = '정보 없음';
          }
        });
      });
    },
    calculateTotalCost(recipe) {
      const materialCost = recipe.materials.reduce((sum, material) => {
        return sum + (material.unitPrice === '정보 없음' ? 0 : material.unitPrice * material.quantity);
      }, 0);
      return materialCost + recipe.gold;
    },
    calculateUnitCost(recipe) {
      const totalCost = this.calculateTotalCost(recipe);
      return (totalCost / recipe.quantity).toFixed(2);
    },
    getMethodLabel(method) {
      const methods = {
        '수렵': '(수렵)',
        '낚시': '(낚시)',
        '고고학': '(고고학)',
      };
      return methods[method];
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

.crafting-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.crafting-card {
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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
</style>
