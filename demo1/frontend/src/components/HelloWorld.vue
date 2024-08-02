<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <button @click="fetchItems">데이터 가져오기</button>
    <div v-if="items.length">
      <h2>API 데이터:</h2>
      <ul>
        <li v-for="item in items" :key="item.Id">
          <img :src="item.Icon" :alt="item.Name" class="item-icon"/>
          <p>이름: {{ item.Name }}</p>
          <p>등급: {{ item.Grade }}</p>
          <p>어제 평균 가격: {{ item.YDayAvgPrice }}</p>
          <p>최근 가격: {{ item.RecentPrice }}</p>
          <p>현재 최소 가격: {{ item.CurrentMinPrice }}</p>
          <p>최근 거래량: {{ item.TradeCount || 'N/A' }}</p>
          <p>현재 최소 가격 + 1골드 거래량: {{ item.HigherPriceTradeCount || 'N/A' }}</p>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import axiosInstance from '../axiosInstance';

export default {
  name: 'HelloWorld',
  props: {
    msg: String
  },
  data() {
    return {
      items: [],
    };
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
          item.HigherPriceTradeCount = this.calculateHigherPriceTradeCount(details.Stats, item.CurrentMinPrice + 1);
        }

        this.items = items;
        console.log(items);
      } catch (error) {
        console.error('Error fetching items:', error);
      }
    },
    calculateHigherPriceTradeCount(stats, higherPrice) {
      const higherPriceStat = stats.find(stat => stat.AvgPrice === higherPrice);
      return higherPriceStat ? higherPriceStat.TradeCount : 'N/A';
    }
  },
  mounted() {
    this.fetchItems();
  }
}
</script>

<style scoped>
h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: flex;
  align-items: center;
  margin: 10px 0;
}

.item-icon {
  width: 50px;
  height: 50px;
  margin-right: 10px;
}

p {
  margin: 0;
}
</style>
