/*
 * Copyright System Support Inc. LIMITED 2023
 * システム名：SPA在庫管理システム
 * 履歴：
 * 日付　　　　更新者　　　　更新内容
 * 2023/06/29　STS)平野　　　初版
 */
package com.example.demo.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

/**
 * クラス名：セキュリティ設定クラス<br>
 * 概要　　：セキュリティ情報を設定するクラス<br>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	/**
	 * ログイン用フィルターチェイン
	 * @param http
	 * @return フィルターチェイン
	 * @throws Exception
	 */
	@Bean
	SecurityFilterChain loginfilterChain(HttpSecurity http) throws Exception {
		XorCsrfTokenRequestAttributeHandler requestHandler = new XorCsrfTokenRequestAttributeHandler();
		// set the name of the attribute the CsrfToken will be populated on
		requestHandler.setCsrfRequestAttributeName(null);
		// 設定
		http.formLogin(login -> login														// フォーム認証の記述開始開始
				.loginPage("/login").permitAll()											// ログイン画面のURL
				.failureUrl("/fail").permitAll()											// ログイン失敗後のリダイレクトURL
				.successForwardUrl("/todo")								// ログイン成功後のリダイレクトURL
		).logout(logout -> logout															// ログアウトの設定記述開始
				.logoutSuccessUrl("/logout").permitAll()								// ログアウト成功後のリダイレクトURL
				.invalidateHttpSession(true)												// セッション削除
		).csrf(csrf -> csrf
				.ignoringRequestMatchers("/api/csrf")
				.ignoringRequestMatchers("/login")
				.csrfTokenRequestHandler(getCsrfTokenRequestHandler())
//				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		).authorizeHttpRequests(authz -> authz
				.requestMatchers("/").permitAll()
				.requestMatchers("/login").permitAll()
				.requestMatchers("/logout").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/csrf").permitAll()
				.requestMatchers("/login**", "/callback/", "/error**", "/msLogin*", "/img/**", "/img/**", "/spaLogout").permitAll()
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // "css/**"などはログイン無しでもアクセス可能
				.anyRequest().authenticated()
		).headers(headers -> headers
				.frameOptions(frameOptions -> frameOptions
						.sameOrigin())
				.httpStrictTransportSecurity(hsts -> hsts
						.preload(true))
		).cors(cors -> cors.configurationSource(this.corsConfigurationSource())
	);
		return http.build();
	}

	private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.addAllowedOrigin("http://localhost:5173");
		corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
		corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", corsConfiguration);
        return corsSource;
    }
	
	/**
	 * Csrf対策用パスワード暗号化
	 * @return Csrfトークンハンドラー
	 */
	@Bean
	CsrfTokenRequestAttributeHandler getCsrfTokenRequestHandler() {
		CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
		// set the name of the attribute the CsrfToken will be populated on
		requestHandler.setCsrfRequestAttributeName(null);
		return requestHandler;
	}

	/**
	 * パスワードをBCryptで暗号化するクラス
	 * @return パスワードをBCryptで暗号化するクラスオブジェクト
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

