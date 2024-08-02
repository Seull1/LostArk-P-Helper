<template>
  <div class="container">
    <div class="left-pane">
      <h1>오레하 아이템 목록</h1>
      <div v-if="items.length">
        <ul>
          <li v-for="item in items" :key="item.Id" class="item">
            <img :src="item.Icon" :alt="item.Name" class="item-icon"/>
            <div class="item-details">
              <h3>{{ item.Name }}</h3>
              <p>현재 최소 가격: {{ item.CurrentMinPrice }} 골드</p>
            </div>
          </li>
        </ul>
      </div>
      <p v-else>오레하 관련 데이터가 없습니다.</p>
    </div>
    <div class="right-pane">
      <h2>오레하 제작 비용 계산기</h2>
      <div class="cost-box" v-for="(cost, index) in calculatedCosts" :key="index">
        <div class="cost-item">
          <img :src="getItemIcon(cost.type)" :alt="cost.type" class="cost-item-icon" />
          <div class="cost-details">
            <h3>{{ cost.type }} <span class="method">{{ getMethodLabel(cost.method) }}</span></h3>
            <p>1개 제작 비용: {{ (cost.price / cost.quantity).toFixed(2) }} 골드</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axiosInstance from '../axiosInstance';

export default {
  data() {
    return {
      items: [],
      calculatedCosts: [],
    };
  },
  async mounted() {
    await this.fetchItems();
    await this.calculateCosts();
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

        this.items = response.data.Items;
      } catch (error) {
        console.error('Error fetching items:', error);
      }
    },
    async calculateCosts() {
      const recipes = [
        {
          type: '오레하 융화 재료',
          method: '수렵',
          materials: [
            { name: '오레하 두툼한 생고기', quantity: 10 },
            { name: '다듬은 생고기', quantity: 40 },
            { name: '두툼한 생고기', quantity: 80 },
            { name: '골드', quantity: 205 }
          ],
          quantity: 30,
        },
        {
          type: '오레하 융화 재료',
          method: '낚시',
          materials: [
            { name: '오레하 태양 잉어', quantity: 10 },
            { name: '붉은 살 생선', quantity: 40 },
            { name: '생선', quantity: 80 },
            { name: '골드', quantity: 80 }
          ],
          quantity: 30,
        },
        {
          type: '오레하 융화 재료',
          method: '고고학',
          materials: [
            { name: '오레하 유물', quantity: 8 },
            { name: '희귀한 유물', quantity: 26 },
            { name: '고대 유물', quantity: 64 },
            { name: '골드', quantity: 205 }
          ],
          quantity: 30,
        },
        {
          type: '상급 오레하 융화 재료',
          method: '수렵',
          materials: [
            { name: '오레하 두툼한 생고기', quantity: 16 },
            { name: '다듬은 생고기', quantity: 64 },
            { name: '두툼한 생고기', quantity: 128 },
            { name: '골드', quantity: 250 }
          ],
          quantity: 20,
        },
        {
          type: '상급 오레하 융화 재료',
          method: '낚시',
          materials: [
            { name: '오레하 태양 잉어', quantity: 16 },
            { name: '붉은 살 생선', quantity: 64 },
            { name: '생선', quantity: 128 },
            { name: '골드', quantity: 250 }
          ],
          quantity: 20,
        },
        {
          type: '상급 오레하 융화 재료',
          method: '고고학',
          materials: [
            { name: '오레하 유물', quantity: 16 },
            { name: '희귀한 유물', quantity: 29 },
            { name: '고대 유물', quantity: 94 },
            { name: '골드', quantity: 250 }
          ],
          quantity: 20,
        },
        {
          type: '최상급 오레하 융화 재료',
          method: '수렵',
          materials: [
            { name: '오레하 두툼한 생고기', quantity: 52 },
            { name: '다듬은 생고기', quantity: 69 },
            { name: '두툼한 생고기', quantity: 142 },
            { name: '골드', quantity: 300 }
          ],
          quantity: 15,
        },
        {
          type: '최상급 오레하 융화 재료',
          method: '낚시',
          materials: [
            { name: '오레하 태양 잉어', quantity: 52 },
            { name: '붉은 살 생선', quantity: 69 },
            { name: '생선', quantity: 142 },
            { name: '골드', quantity: 300 }
          ],
          quantity: 15,
        },
        {
          type: '최상급 오레하 융화 재료',
          method: '고고학',
          materials: [
            { name: '오레하 유물', quantity: 52 },
            { name: '희귀한 유물', quantity: 51 },
            { name: '고대 유물', quantity: 107 },
            { name: '골드', quantity: 300 }
          ],
          quantity: 15,
        }
      ];

      for (const recipe of recipes) {
        let totalCost = 0;
        for (const material of recipe.materials) {
          if (material.name === '골드') {
            totalCost += material.quantity;
          } else {
            const item = await this.fetchMaterialPrice(material.name);
            if (item && item.CurrentMinPrice !== undefined) {
              totalCost += item.CurrentMinPrice * material.quantity;
            } else {
              console.warn(`Item not found or price unavailable for: ${material.name}`);
              totalCost += 0; // 재료 가격이 없을 경우 0으로 처리
            }
          }
        }
        this.calculatedCosts.push({ type: recipe.type, method: recipe.method, price: totalCost, quantity: recipe.quantity });
      }
    },
    async fetchMaterialPrice(materialName) {
      try {
        const response = await axiosInstance.post('/markets/items', {
          ItemName: materialName,
          Sort: 'GRADE',
          CategoryCode: 50010,
          PageNo: 1,
          SortCondition: 'ASC',
        });

        return response.data.Items[0];
      } catch (error) {
        console.error(`Error fetching material price for ${materialName}:`, error);
        return null; // 오류 발생 시 null 반환
      }
    },
    getItemIcon(type) {
      const icons = {
        '오레하 융화 재료': 'https://cdn-lostark.game.onstove.com/efui_iconatlas/use/use_9_71.png',
        '상급 오레하 융화 재료': 'https://cdn-lostark.game.onstove.com/efui_iconatlas/use/use_8_109.png',
        '최상급 오레하 융화 재료': 'https://cdn-lostark.game.onstove.com/efui_iconatlas/use/use_11_29.png',
      };
      return icons[type]; // 아이콘 경로 설정
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
.container {
  display: flex;
}
.left-pane, .right-pane {
  width: 50%;
  padding: 20px;
}
.item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.item-icon {
  width: 50px;
  height: 50px;
  margin-right: 10px;
}
.item-details {
  display: flex;
  flex-direction: column;
}
.cost-box {
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
}
.cost-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.cost-item-icon {
  width: 50px;
  height: 50px;
  margin-right: 10px;
}
.cost-details {
  display: flex;
  flex-direction: column;
}
.method {
  font-weight: normal;
  font-size: 14px;
}


</style>