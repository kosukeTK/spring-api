/*
 * Copyright System Support Inc. LIMITED 2023
 * システム名：SPA在庫管理システム
 * 履歴：
 * 日付　　　　更新者　　　　更新内容
 * 2023/06/29　STS)平野　　　初版
 */
package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * クラス名：共通コンフィグレーションクラス<br>
 * 概要　　：コントローラにインターセプターの設定共通処理を行うクラスです。<br>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080") // 許可するオリジン
                .allowedMethods("GET", "POST", "PUT", "DELETE"); // 許可するHTTPメソッド
    }
}
