package com.example.gym.service;

import com.example.gym.order.Order;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentService {
    public Map<String, Object> createPaymentIntent(Order order) {
        return Map.of("provider", "mock", "prepay_id", "mock_prepay_" + order.getOrderNo());
    }
}