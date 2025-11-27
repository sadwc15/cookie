const { request } = require('../../../utils/request.js');

Page({
  data:{ list:[], loading:true },
  onShow(){ this.fetch(); },
  async fetch(){
    this.setData({loading:true});
    try{
      const data = await request('/trainers');
      const rows = Array.isArray(data) ? data : (data && data.list) ? data.list : [];
      const list = rows.map(it => ({
        ...it,
        rateYuan: (Number(it.ratePerHour || 0)/100).toFixed(2),
        avatarText: (it.name || '教').slice(0,1)
      }));
      this.setData({list, loading:false});
    }catch(e){
      console.error('[TRAINERS FETCH ERROR]', e);
      this.setData({loading:false});
      wx.showToast({ title:'加载教练失败', icon:'none' });
    }
  },
  goDetail(e){
    const { id } = e.currentTarget.dataset;
    if (!id) return;
    wx.navigateTo({ url: `/pages/trainers/detail/detail?id=${id}` });
  }
});