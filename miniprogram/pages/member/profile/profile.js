Page({
  data: {
    user: null,
    displayName: '游客',
    entries: [
      { title: '我的收藏', url: '/pages/member/favorites/list' },
      { title: '历史浏览', url: '/pages/member/history/list' },
      { title: '教练入口', url: '/pages/trainers/list/list' },
      { title: '系统后台管理', url: '/pages/admin/dashboard/dashboard' },
      { title: '数据统计与分析', url: '/pages/analytics/dashboard/dashboard' }
    ]
  },
  onShow() {
    const user = wx.getStorageSync('user') || null;
    const displayName = user ? (user.nickname || user.username || '游客') : '游客';
    this.setData({ user, displayName });
  },
  go(e) {
    const { url } = e.currentTarget.dataset;
    if (!url) return;
    if (url.indexOf('/pages/admin/') === 0) {
      const role = wx.getStorageSync('user_role');
      if (role !== 'admin') {
        wx.showToast({ title: '仅管理员可访问', icon: 'none' });
        return;
      }
    }
    wx.navigateTo({ url });
  },
  logout() {
    wx.removeStorageSync('sessionToken');
    wx.removeStorageSync('user');
    wx.removeStorageSync('user_role');
    wx.showToast({ title:'已退出登录', icon:'none' });
    setTimeout(()=> wx.switchTab({ url:'/pages/index/index'}), 500);
  }
});