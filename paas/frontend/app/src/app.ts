import AppService from './services/appService';
import { util } from '@sreworks/shared-utils';
import { Property as util_properties } from '@sreworks/shared-utils';
import { Property as components_properties } from '@sreworks/components';
import { Property as widgets_properties } from '@sreworks/widgets';
import { Property as frame_properties } from '@sreworks/framework';
import properties from './properties';
//不能去掉用于引入less.js来换肤使用! window.less.xxx 会有用，需要加载进来，但不是这里使用
import less from 'less';

declare const THEMES: any;
import 'antd/dist/antd.css';
import 'react-grid-layout/css/styles.css';
import 'react-resizable/css/styles.css';
import './index.less';
import '@sreworks/framework/dist/theme/index.css';
import '@sreworks/widgets/dist/theme/index.css';
import '@sreworks/components/dist/theme/index.css';

// 注入属性
util_properties.injectProperties(properties);
components_properties.injectProperties(properties);
widgets_properties.injectProperties(properties);
frame_properties.injectProperties(properties);

// 显式挂载 less 到 window：less 浏览器版加载时会把 window.less 替换为完整 API（含 modifyVars），
// 这里显式引用 `less` 防止被 webpack tree-shaking 掉
;(window as any).less = less

// 主题初始化
let themeType = localStorage.getItem('sreworks-theme');
if (!themeType) {
  localStorage.setItem('sreworks-theme', 'light');
}
if (themeType === 'dark') {
  themeType = 'navyblue';
  localStorage.setItem('sreworks-theme', 'navyblue');
}
if (themeType === 'navyblue' && (window as any).less) {
  (window as any).less.modifyVars(THEMES[themeType]);
}

// 运行时配置
export async function getInitialState() {
  const params = {
    namespaceId: (util.getNewBizApp()?.split(',')[1]) || '',
    stageId: (util.getNewBizApp()?.split(',')[2]) || '',
    visit: true,
  };

  try {
    const isLogined = await AppService.isLogined();
    if (isLogined && (isLogined as any)['name']) {
      const res = await AppService.getAllProfile(params);
      const { collectList = [], customQuickList = [], workspaces } = res.profile;
      return {
        workspaces,
        collectList,
        customQuickList,
        isEqual: res.isEqual,
      };
    }
  } catch (error) {
    console.error(error);
  }
  return {};
}

// dva 配置
export const dva = {
  config: {
    onError(err: Error) {
      console.error(err.message);
    },
  },
};
