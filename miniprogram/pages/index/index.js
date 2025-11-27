const tabBarPages = [
  '/pages/index/index',
  '/pages/classes/list/list',
  '/pages/bookings/list/list',
  '/pages/member/profile/profile'
];

Page({
  data: {
    navItems: [
      { title: '课程',      icon: 'success',      color: '#FFC107', url: '/pages/classes/list/list',    desc: '团课 / 私教' },
      { title: '教练',      icon: 'info',         color: '#FFC107', url: '/pages/trainers/list/list',   desc: '专业团队' },
      { title: '优惠券',    icon: 'warn',         color: '#FFC107', url: '/pages/marketing/coupons/coupons', desc: '领取 / 使用' },
      { title: '我的预约',  icon: 'waiting',      color: '#FFC107', url: '/pages/bookings/list/list',  desc: '预约记录' },
      { title: '订单',      icon: 'safe_success', color: '#FFC107', url: '/pages/orders/list/list',    desc: '支付记录' },
      { title: '我的',      icon: 'safe_warn',    color: '#FFC107', url: '/pages/member/profile/profile', desc: '个人信息' },
      { title: '统计分析',  icon: 'search',       color: '#FFC107', url: '/pages/analytics/dashboard/dashboard', desc: '指标与趋势' }
    ]
  },

  onNavTap(e) {
    const { url } = e.currentTarget.dataset;
    if (!url) return;
    if (tabBarPages.includes(url)) {
      wx.switchTab({ url });
    } else {
      wx.navigateTo({ url });
    }
  }
});