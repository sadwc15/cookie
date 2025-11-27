package com.example.gym.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Api<T> {
    private T data;
    private String error;

    public static <T> Api<T> ok(T data) {
        return new Api<>(data, null);
    }

    public static <T> Api<T> err(String msg) {
        return new Api<>(null, msg);
    }
}