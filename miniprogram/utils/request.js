const { API_BASE } = require('../config.js');

function getSessionToken() {
  try { return wx.getStorageSync('sessionToken') || ''; } catch { return ''; }
}

function isLoginRoute() {
  const pages = getCurrentPages();
  const current = pages && pages[pages.length - 1];
  return current && current.route === 'pages/auth/account/account';
}

function request(path, { method = 'GET', data = {}, header = {} } = {}) {
  return new Promise((resolve, reject) => {
    const url = `${API_BASE}${path}`;
    wx.request({
      url,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': getSessionToken() ? `Bearer ${getSessionToken()}` : '',
        ...header
      },
      success(res) {
        if (res.statusCode === 401) {
          wx.showToast({ title: '请先登录', icon: 'none' });
          if (!isLoginRoute()) {
            wx.navigateTo({ url: '/pages/auth/account/account' });
          }
          reject(res.data || { error: 'unauthorized' });
          return;
        }
        if (res.statusCode >= 400) {
          wx.showToast({ title: (res.data && (res.data.error || res.data.message)) || `请求失败(${res.statusCode})`, icon: 'none' });
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

module.exports = { request };