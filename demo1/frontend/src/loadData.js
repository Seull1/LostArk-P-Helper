// src/loadData.js
import axiosInstance from './axiosInstance';

async function fetchData() {
    try {
        const response = await axiosInstance.get('/markets/items');
        return response.data.Items;
    } catch (error) {
        console.error('Error fetching data:', error);
        return [];
    }
}

export default fetchData;
