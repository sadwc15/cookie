const { request } = require('../../../utils/request.js');

Page({
  data: { list: [], loading: true },
  onShow() { this.fetch(); },
  async fetch() {
    this.setData({ loading: true });
    try {
      const data = await request('/bookings'); // 确保后端提供该接口
      const rows = Array.isArray(data) ? data : (data && data.list) ? data.list : [];
      const list = rows.map(it => ({
        ...it,
        timeText: `${it.startTime} - ${it.endTime}`
      }));
      this.setData({ list, loading: false });
    } catch (e) {
      console.error('[BOOKINGS FETCH ERROR]', e);
      this.setData({ loading: false });
      wx.showToast({ title: '加载预约失败', icon: 'none' });
    }
  }
});