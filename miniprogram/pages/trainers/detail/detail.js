const { request } = require('../../../utils/request.js');

Page({
  data: {
    loading: true,
    trainer: null,      // { id, name, bio, avatarText, rateYuan }
    slots: [],          // [{ id, startTime, endTime, remain, statusText, selectable }]
    selectedSlotId: null,
    errorText: ''
  },

  onLoad(options) {
    const { id } = options || {};
    if (!id) {
      wx.showToast({ title: '缺少教练ID', icon: 'none' });
      return;
    }
    this.trainerId = id;
  },

  onShow() {
    if (this.trainerId) {
      this.fetch();
    }
  },

  async fetch() {
    this.setData({ loading: true, errorText: '' });
    try {
      // 后端接口：GET /trainers/{id}/schedule 约定返回 { trainer: {...}, slots: [...] }
      const data = await request(`/trainers/${this.trainerId}/schedule`);
      const rawTrainer = data.trainer || {};
      const trainer = {
        id: rawTrainer.id,
        name: rawTrainer.name || '',
        bio: rawTrainer.bio || '',
        rateYuan: (Number(rawTrainer.ratePerHour || 0) / 100).toFixed(2),
        avatarText: (rawTrainer.name || '教').substr(0, 1)
      };

      const rawSlots = Array.isArray(data.slots)
        ? data.slots
        : (data.slots && Array.isArray(data.slots.list)) ? data.slots.list : [];

      const slots = rawSlots.map(s => {
        const capacity = Number(s.capacity || 0);
        const booked = Number(s.booked || 0);
        const remain = Math.max(0, capacity - booked);
        return {
          id: s.id,
            // 保持后端提供格式即可，不再在 WXML 做拼接计算
          startTime: s.startTime || '',
          endTime: s.endTime || '',
          remain,
          statusText: remain > 0 ? '可约' : '已满',
          selectable: remain > 0
        };
      });

      this.setData({
        trainer,
        slots,
        loading: false
      });
    } catch (e) {
      console.error('[SCHEDULE FETCH ERROR]', e);
      this.setData({ loading: false });
      wx.showToast({ title: '加载排期失败', icon: 'none' });
    }
  },

  selectSlot(e) {
    const { id } = e.currentTarget.dataset;
    // 找到该时段是否可选
    const slot = this.data.slots.find(s => String(s.id) === String(id));
    if (!slot || !slot.selectable) {
      wx.showToast({ title: '该时段已满', icon: 'none' });
      return;
    }
    this.setData({
      selectedSlotId: id,
      errorText: '' // 清除“未选定”提示
    });
  },

  goCreate() {
    const { selectedSlotId, trainer } = this.data;
    if (!selectedSlotId) {
      this.setData({ errorText: '未选定预约时段' });
      return;
    }
    const query = `trainerId=${trainer.id}&slotId=${selectedSlotId}`;
    wx.navigateTo({ url: `/pages/bookings/create/create?${query}` });
  }
});