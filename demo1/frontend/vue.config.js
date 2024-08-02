const { defineConfig } = require('@vue/cli-service')
module.exports = {
  outputDir: "../src/main/resources/static",   //빌드타겟 다렉토리
  devServer: {
    port: 8082,
    proxy: {
      '/example': {
        // '/api' 로 들어오면 포트 80801(스프링 서버) 로 보낸다
        target: 'http://localhost:8081',
        changeOrigin: true  //cross origin 허용
      }
    }
  }
};