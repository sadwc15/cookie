const { request } = require('../../../utils/request.js');

Page({
  data: { list: [], loading: true },
  onShow() { this.fetch(); },
  async fetch() {
    this.setData({ loading: true });
    try {
      const data = await request('/classes');
      const rows = Array.isArray(data) ? data : (data && data.list) ? data.list : [];
      const list = rows.map(it => ({
        ...it,
        priceYuan: (Number(it.price || 0) / 100).toFixed(2)
      }));
      this.setData({ list, loading: false });
    } catch (e) {
      this.setData({ loading: false });
      wx.showToast({ title: '加载课程失败', icon: 'none' });
    }
  },
  goDetail(e) {
    const { id } = e.currentTarget.dataset;
    if (!id) return;
    wx.navigateTo({ url: `/pages/classes/detail/detail?id=${id}` });
  }
});