const { request } = require('../../../utils/request.js');

Page({
  data: {
    cls: null,
    slot: null,
    form: { contactName: '', contactPhone: '', note: '' },
    submitting: false
  },

  async onLoad(options) {
    const { classId, slotId } = options || {};
    if (!classId || !slotId) {
      wx.showToast({ title: '缺少参数', icon: 'none' });
      return;
    }
    this.classId = classId;
    this.slotId = slotId;
    // 拉取课程信息以显示顶部box（也可复用上一页传来的数据）
    try {
      const data = await request(`/classes/${classId}`);
      const rows = Array.isArray(data.schedule) ? data.schedule : (data.schedule && data.schedule.list) ? data.schedule.list : [];
      const slot = rows.find(s => String(s.slotId || s.id) === String(slotId)) || null;
      const cls = { id: data.id, title: data.title || '' };
      this.setData({ cls, slot });
    } catch(e) {
      wx.showToast({ title: '加载预约信息失败', icon: 'none' });
    }
  },

  onInputName(e) { this.setData({ 'form.contactName': e.detail.value }); },
  onInputPhone(e) { this.setData({ 'form.contactPhone': e.detail.value }); },
  onInputNote(e) { this.setData({ 'form.note': e.detail.value }); },

  async submit() {
    const token = wx.getStorageSync('sessionToken');
    if (!token) {
      wx.navigateTo({ url: '/pages/auth/account/account' });
      return;
    }
    const { contactName, contactPhone } = this.data.form;
    if (!contactName || !contactPhone) {
      wx.showToast({ title: '请填写姓名和电话', icon: 'none' });
      return;
    }
    this.setData({ submitting: true });
    try {
      await request('/bookings', {
        method: 'POST',
        data: {
          classId: Number(this.classId),
          slotId: Number(this.slotId),
          contactName,
          contactPhone,
          note: this.data.form.note || ''
        }
      });
      wx.showToast({ title: '预约成功', icon: 'success' });
      setTimeout(() => wx.switchTab({ url: '/pages/bookings/list/list' }), 600);
    } catch (e) {
      wx.showToast({ title: (e && e.error) ? e.error : '预约失败', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  }
});