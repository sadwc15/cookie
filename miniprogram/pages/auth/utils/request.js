import { API_BASE } from '../pages/auth/config';

function getToken() {
  try { return wx.getStorageSync('token') || ''; } catch { return ''; }
}

export function request(path, { method = 'GET', data = {}, header = {} } = {}) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${API_BASE}${path}`,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': getToken() ? `Bearer ${getToken()}` : '',
        ...header
      },
      success(res) {
        if (res.statusCode === 401) {
          wx.showToast({ title: '请先登录', icon: 'none' });
          wx.navigateTo({ url: '/pages/auth/login/login' });
          return;
        }
        if (res.statusCode >= 400) {
          wx.showToast({ title: res.data?.error || '请求失败', icon: 'none' });
          reject(res.data);
        } else {
          resolve(res.data);
        }
      },
      fail(err) {
        wx.showToast({ title: '网络错误', icon: 'none' });
        reject(err);
      }
    });
  });
}