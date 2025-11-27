const { request } = require('../../../utils/request.js');

Page({
  data: {
    mode: 'login', // 'login' 或 'register'
    username: '',
    password: '',
    nickname: '',
    loading: false
  },

  switchMode() {
    this.setData({ mode: this.data.mode === 'login' ? 'register' : 'login' });
  },

  onInputUsername(e) { this.setData({ username: e.detail.value }); },
  onInputPassword(e) { this.setData({ password: e.detail.value }); },
  onInputNickname(e) { this.setData({ nickname: e.detail.value }); },

  async submit() {
    if (!this.data.username || !this.data.password) {
      wx.showToast({ title: '请输入账号和密码', icon: 'none' });
      return;
    }
    this.setData({ loading: true });
    try {
      if (this.data.mode === 'register') {
        await request('/auth/register', { method: 'POST', data: {
          username: this.data.username,
          password: this.data.password,
          nickname: this.data.nickname || this.data.username
        }});
        wx.showToast({ title: '注册成功', icon: 'success' });
        this.setData({ mode: 'login' });
      } else {
        const data = await request('/auth/login', { method: 'POST', data: {
          username: this.data.username,
          password: this.data.password
        }});
        if (!data || !data.sessionToken) {
          wx.showToast({ title: '登录失败', icon: 'none' });
          return;
        }
        wx.setStorageSync('sessionToken', data.sessionToken);
        wx.setStorageSync('user', data.user);
        wx.setStorageSync('user_role', data.user.role || 'member');
        wx.showToast({ title: '登录成功', icon: 'success' });
        setTimeout(() => {
          // 返回上一页（通常是课程详情或预约页）
          wx.navigateBack();
        }, 500);
      }
    } catch (e) {
      wx.showToast({ title: (e && e.error) ? e.error : '操作失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  }
});