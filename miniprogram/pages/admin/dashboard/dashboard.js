const { request } = require('../../../utils/request.js');

Page({
  data: { loading: true, stats: null },
  async onShow() {
    this.setData({ loading: true });
    try {
      const data = await request('/analytics/overview');
      const orders = Number(data.orders || 0);
      const classConsumption = Number(data.classConsumption || 0);
      const occupancyRate = Number(data.occupancyRate || 0); // 0~1
      const waitlistConversionRate = Number(data.waitlistConversionRate || 0); // 0~1

      const occupancyPct = Math.round(occupancyRate * 1000) / 10; // 保留1位
      const waitlistPct = Math.round(waitlistConversionRate * 1000) / 10;

      const stats = {
        orders,
        classConsumption,
        occupancyRate,
        waitlistConversionRate,
        occupancyPct,                  // 例如 78.5
        waitlistPct,                   // 例如 42.0
        occupancyText: occupancyPct.toFixed(1) + '%',
        waitlistText: waitlistPct.toFixed(1) + '%'
      };
      this.setData({ stats, loading: false });
    } catch (e) {
      this.setData({ loading: false });
      wx.showToast({ title: '加载统计失败', icon: 'none' });
    }
  }
});