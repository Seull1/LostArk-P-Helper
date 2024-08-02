import { createApp } from 'vue';
import App from './App.vue';
import axios from 'axios';

const app = createApp(App);

console.log('API Token:', process.env.VUE_APP_API_TOKEN);

const instance = axios.create({
    baseURL: 'https://developer-lostark.game.onstove.com',
    headers: {
        'content-type': 'application/json;charset=UTF-8',
        accept: 'application/json',
    },
});

instance.interceptors.request.use(
    function (config) {
        config.headers.authorization = `Bearer ${process.env.VUE_APP_API_TOKEN}`;
        return config;
    },
    function (error) {
        return Promise.reject(error);
    }
);

app.config.globalProperties.$axios = instance;

app.mount('#app');
