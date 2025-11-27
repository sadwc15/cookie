const { request } = require('../../../utils/request.js');

Page({
  data: { list: [], loading: true },
  onShow() { this.fetch(); },

  async fetch() {
    this.setData({ loading: true });
    try {
      const data = await request('/coupons/available');
      const rows = Array.isArray(data) ? data : (data && data.list) ? data.list : [];
      const list = rows.map(it => ({
        ...it,
        valueText: it.type === 'amount'
          ? `¥${(Number(it.value || 0) / 100).toFixed(2)}`
          : `${100 - Number(it.value || 0)}折`,
        minSpendText: (Number(it.minSpend || 0) / 100).toFixed(2),
        remain: Math.max(0, Number(it.total || 0) - Number(it.claimed || 0)),
        claimedByMe: Boolean(it.claimedByMe) // 如果后端返回用户是否已领取
      }));
      this.setData({ list, loading: false });
    } catch (e) {
      console.error('[COUPONS FETCH ERROR]', e);
      this.setData({ loading: false });
      wx.showToast({ title: '加载优惠券失败', icon: 'none' });
    }
  },

  async claim(e) {
    const { id, index } = e.currentTarget.dataset;
    const token = wx.getStorageSync('token');
    if (!token) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      wx.navigateTo({ url: '/pages/auth/login/login' });
      return;
    }
    try {
      // 调用后端领取接口
      await request(`/coupons/${id}/claim`, { method: 'POST' });
      // 前端状态更新：标记为已领取，remain - 1
      const list = [...this.data.list];
      const item = { ...list[index] };
      item.claimedByMe = true;
      if (item.remain > 0) item.remain = item.remain - 1;
      list[index] = item;
      this.setData({ list });
      wx.showToast({ title: '已领取', icon: 'success' });
    } catch (err) {
      wx.showToast({ title: (err && err.error) ? err.error : '领取失败', icon: 'none' });
    }
  }
});