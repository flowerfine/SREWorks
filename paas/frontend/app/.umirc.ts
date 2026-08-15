import { defineConfig } from 'umi';

export default defineConfig({
  dva: {
    immer: true,
    hmr: true,
  },
  antd: {},
  hash: true,
  history: { type: 'hash' },
  publicPath: process.env.NODE_ENV === 'production' ? './' : '/',
  outputPath: 'build',

  alias: {
    '@': './src',
  },

  lessLoader: {
    modifyVars: {},
    javascriptEnabled: true,
  },

  externals: {
    'react': 'React',
    'react-dom': 'ReactDOM',
    'antd': 'antd',
    'moment': 'moment',
    'moment-duration-format': 'moment-duration-format',
    'systemjs': 'systemjs',
    'element-ui': 'ELEMENT',
    'vue': 'Vue',
    'vuera': 'vuera',
    'bizcharts': 'BizCharts',
    'lodash': '_',
    'html2canvas': 'html2canvas',
    'jquery': 'jQuery',
  },

  proxy: {
    '/gateway': {
      target: 'http://localhostl:8080',  // 填入实际后端地址
      changeOrigin: true,
      cookieDomainRewrite: 'localhost',
    },
  },

  routes: [
    {
      path: '/',
      component: '@/application/Application',
      routes: [
        { path: '/', component: '@/pages/index' },
        { component: '@/pages/404' },
      ],
    },
  ],
});
