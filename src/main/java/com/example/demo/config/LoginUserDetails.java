/*
 * Copyright System Support Inc. LIMITED 2023
 * システム名：SPA在庫管理システム
 * 履歴：
 * 日付　　　　更新者　　　　更新内容
 * 2023/06/29　STS)平野　　　初版
 */
package com.example.demo.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.repository.entity.TblMUserEntity;

/**
 * クラス名：ログイン認証ユーザークラス<br>
 * 概要　　：ログイン認証を保持するクラス<br>
 */
public class LoginUserDetails implements UserDetails {
	/** シリアライズバージョンID */
	private static final long serialVersionUID = 1L;
	/** TblMUserテーブルから取得したオブジェクトを格納 */
	private final TblMUserEntity tblMUserEntity;

	/**
	 * コンストラクタ
	 * @param tblMUserEntity
	 */
	public LoginUserDetails(TblMUserEntity tblMUserEntity) {
		this.tblMUserEntity = tblMUserEntity;
	}

	/**
	 * オーバライドメソッド
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	/**
	 * オーバライドメソッド
	 */
	@Override
	public String getPassword() {
		return tblMUserEntity.getPassword();
	}

	/**
	 * オーバライドメソッド
	 */
	@Override
	public String getUsername() {
		return tblMUserEntity.getIdUser();
	}

	/**
	 * オーバライドメソッド
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * オーバライドメソッド
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * オーバライドメソッド
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * オーバライドメソッド
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}
}