/*
 * Copyright System Support Inc. LIMITED 2023
 * システム名：SPA在庫管理システム
 * 履歴：
 * 日付　　　　更新者　　　　更新内容
 * 2023/06/29　STS)平野　　　初版
 */
package com.example.demo.config;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrations;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;
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

	@Value("classpath:credentials/rp-private.key")
	RSAPrivateKey privateKey;

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
		http.formLogin(login -> login // フォーム認証の記述開始開始
				.loginPage("/login").permitAll() // ログイン画面のURL
				.failureUrl("/fail").permitAll() // ログイン失敗後のリダイレクトURL
				.successForwardUrl("/todo") // ログイン成功後のリダイレクトURL
		).logout(logout -> logout // ログアウトの設定記述開始
				.logoutSuccessUrl("/logout").permitAll() // ログアウト成功後のリダイレクトURL
				.invalidateHttpSession(true) // セッション削除
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
				.requestMatchers("/login**", "/callback/", "/error**", "/msLogin*", "/img/**", "/img/**", "/spaLogout")
				.permitAll()
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // "css/**"などはログイン無しでもアクセス可能
				.anyRequest().authenticated())
				.saml2Login(Customizer.withDefaults())
				.saml2Logout(Customizer.withDefaults())
				.headers(headers -> headers
						.frameOptions(frameOptions -> frameOptions
								.sameOrigin())
						.httpStrictTransportSecurity(hsts -> hsts
								.preload(true)))
				.cors(cors -> cors.configurationSource(this.corsConfigurationSource()));
		return http.build();
	}

	//	@Bean
	//	private RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
	//		return new InMemoryRelyingPartyRegistrationRepository(relyingPartyRegistration());
	//	}
	//
	//	@Bean
	//	private RelyingPartyRegistration relyingPartyRegistration() {
	//		return RelyingPartyRegistration.withRegistrationId("your-registration-id")
	//				.entityId("your-sp-entity-id")
	//				.assertionConsumerServiceLocation("http://your-sp-url/saml/SSO")
	//				.assertingPartyDetails(entrustingPartyDetails -> entrustingPartyDetails
	//						.entityId("your-idp-entity-id")
	//						.singleSignOnServiceLocation("http://idp-sso-service-url")
	//						.singleLogoutServiceLocation("http://idp-slo-service-url"))
	//				.signingX509Credentials(credentials -> credentials
	//						.add(Saml2X509Credential.decryption(this.privateKey, relyingPartyCertificate()))
	//								.signingKey("classpath:/path/to/your-sp-private-key.pem")
	//								.verificationKey("classpath:/path/to/your-sp-certificate.pem")))
	//				.build();
	//	}
	//	
	//	X509Certificate relyingPartyCertificate() {
	//		Resource resource = new ClassPathResource("credentials/rp-certificate.crt");
	//		try (InputStream is = resource.getInputStream()) {
	//			return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);
	//		}
	//		catch (Exception ex) {
	//			throw new UnsupportedOperationException(ex);
	//		}
	//	}

	@Bean
	RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
		RelyingPartyRegistration relyingPartyRegistration = RelyingPartyRegistrations
				.fromMetadataLocation("https://dev-05937739.okta.com/app/exk46xofd8NZvFCpS5d7/sso/saml/metadata")
				.registrationId("one")
				.decryptionX509Credentials(
						(c) -> c.add(Saml2X509Credential.decryption(this.privateKey, relyingPartyCertificate())))
				.signingX509Credentials(
						(c) -> c.add(Saml2X509Credential.signing(this.privateKey, relyingPartyCertificate())))
				.singleLogoutServiceLocation(
						"https://dev-05937739.okta.com/app/dev-05937739_springgsecuritysaml2idp_1/exk46xofd8NZvFCpS5d7/slo/saml")
				.singleLogoutServiceResponseLocation("http://localhost:8080/logout/saml2/slo")
				.singleLogoutServiceBinding(Saml2MessageBinding.POST).build();

		return new InMemoryRelyingPartyRegistrationRepository(relyingPartyRegistration);
	}

	X509Certificate relyingPartyCertificate() {
		Resource resource = new ClassPathResource("credentials/rp-certificate.crt");
		try (InputStream is = resource.getInputStream()) {
			return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);
		} catch (Exception ex) {
			throw new UnsupportedOperationException(ex);
		}
	}

	// Cors対策
	private CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedMethod("*");
		
		corsConfiguration.addAllowedOrigin("http://localhost:5173");
		corsConfiguration.addAllowedOrigin("http://localhost:8080");
		corsConfiguration.addAllowedOrigin("https://dev-05937739.okta.com");
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
