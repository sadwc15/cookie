package com.example.gym.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WechatService {
    @Value("${app.wechat.appid:}")
    private String appid;
    @Value("${app.wechat.secret:}")
    private String secret;

    public String code2Openid(String code) {
        if (appid == null || appid.isBlank() || secret == null || secret.isBlank()) {
            return "mock_" + code + "_" + UUID.randomUUID().toString().substring(0, 6);
        }
        // 这里可接入正式微信接口：GET https://api.weixin.qq.com/sns/jscode2session ...
        return "mock_" + code + "_" + UUID.randomUUID().toString().substring(0, 6);
    }
}