import { request } from './utils/request';

App({
  globalData: { user: null },
  onLaunch() {
    const token = wx.getStorageSync('token');
    if (token) {
      request('/auth/me').then(res => {
        this.globalData.user = res.user;
      }).catch(() => {});
    }
  }
});