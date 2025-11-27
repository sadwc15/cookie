const { request } = require('../../../utils/request.js'); // 从 pages/orders/list 返回到 utils：两级 → ../../utils；若工具以 pages 为基准，用 ../../../。这里使用 ../../../ 以确保兼容。

Page({
  data: { list: [], loading: true },
  onShow() { this.fetch(); },
  async fetch() {
    this.setData({ loading: true });
    try {
      const data = await request('/orders');
      const list = (data || []).map(it => ({
        ...it,
        amountYuan: (Number(it.amount || 0) / 100).toFixed(2)
      }));
      this.setData({ list, loading: false });
    } catch (e) {
      console.error('[ORDERS FETCH ERROR]', e);
      this.setData({ loading: false });
      wx.showToast({ title: '加载订单失败', icon: 'none' });
    }
  },
  pay(e) {
    const { id } = e.currentTarget.dataset;
    if (!id) return;
    wx.showToast({ title: '支付示例，接入后端后调用支付接口', icon: 'none' });
  }
});