/*
 * Copyright System Support Inc. LIMITED 2023
 * システム名：SPA在庫管理システム
 * 履歴：
 * 日付        更新者       更新内容
 * 2023/06/29  STS)平野     初版
 */
package com.example.demo.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.config.LoginUserDetails;
import com.example.demo.repository.entity.TblMUserEntity;
import com.example.demo.repository.mapper.TblMUserMapper;

import lombok.RequiredArgsConstructor;

/**
 * クラス名：認証ユーザ情報取得クラス<br>
 * 概要　　：ログイン時に認証ユーザーを「TBL_M_USERテーブル」から情報を取得するクラス<br>
 */
@Service
@RequiredArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {

	/** TblMUserDao */
	@Autowired
	private final TblMUserMapper tblMUserMapper;

	/**
	 * オーバライドメソッド
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 入力された名前をキーにTBL_M_USERテーブルのレコードを1件取得
		TblMUserEntity tblMUserEntity = tblMUserMapper.getUserName(username);
		// 該当レコードが取得できなかった場合はエラーにする
		if (tblMUserEntity == null) {
			throw new UsernameNotFoundException("not found : " + username);
		}
		return (new LoginUserDetails(tblMUserEntity));
	}
}
