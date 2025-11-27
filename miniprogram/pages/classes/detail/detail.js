const { request } = require('../../../utils/request.js');

Page({
  data: {
    loading: true,
    cls: null,
    slots: [], // {slotId,startTime,endTime,remain,statusText,statusClass}
    selectedSlotId: null,
    errorText: ''
  },

  async onLoad(options) {
    const { id } = options || {};
    if (!id) {
      wx.showToast({ title: '缺少课程ID', icon: 'none' });
      return;
    }
    this.classId = id;
    await this.fetch();
  },

  async fetch() {
    this.setData({ loading: true, errorText: '' });
    try {
      const data = await request(`/classes/${this.classId}`);
      const cls = {
        id: data.id,
        title: data.title || '',
        desc: data.desc || data.description || '',
        priceYuan: (Number(data.price || 0) / 100).toFixed(2)
      };
      const rows = Array.isArray(data.schedule) ? data.schedule : (data.schedule && data.schedule.list) ? data.schedule.list : [];
      const slots = rows.map(s => {
        const slotId = s.slotId || s.id;
        const remain = Number(s.remain || (Number(s.capacity || 0) - Number(s.booked || 0)) || 0);
        return {
          slotId,
          startTime: s.startTime || '',
          endTime: s.endTime || '',
          remain,
          statusText: remain > 0 ? '可约' : '已满',
          statusClass: remain > 0 ? 'ok' : 'full'
        };
      });
      this.setData({ cls, slots, loading: false });
    } catch (e) {
      this.setData({ loading: false });
      wx.showToast({ title: '加载课程详情失败', icon: 'none' });
    }
  },

  selectSlot(e) {
    const { id } = e.currentTarget.dataset;
    const slot = this.data.slots.find(s => String(s.slotId) === String(id));
    if (!slot || slot.remain <= 0) {
      wx.showToast({ title: '该时段已满', icon: 'none' });
      return;
    }
    this.setData({ selectedSlotId: id, errorText: '' });
  },

  goReserve() {
    if (!this.data.selectedSlotId) {
      this.setData({ errorText: '未选定预约时段' });
      return;
    }
    const token = wx.getStorageSync('sessionToken');
    if (!token) {
      wx.navigateTo({ url: '/pages/auth/account/account' });
      return;
    }
    const query = `classId=${this.classId}&slotId=${this.data.selectedSlotId}`;
    wx.navigateTo({ url: `/pages/bookings/create/create?${query}` });
  }
});