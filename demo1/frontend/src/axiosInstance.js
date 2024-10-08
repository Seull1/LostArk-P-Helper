// src/axiosInstance.js
import axios from 'axios';

const accessToken = process.env.VUE_APP_API_TOKEN;

const instance = axios.create({
    baseURL: 'https://developer-lostark.game.onstove.com',
    headers: {
        'content-type': 'application/json;charset=UTF-8',
        accept: 'application/json',
    },
});

instance.interceptors.request.use(
    function (config) {
        config.headers.authorization = `Bearer ${accessToken}`;
        return config;
    },
    function (error) {
        return Promise.reject(error);
    }
);

export default instance;
