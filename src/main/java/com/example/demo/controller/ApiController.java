package com.example.demo.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    // CSRFトークンを返すためだけのパス
	@PostMapping("/api/csrf")
	public CsrfToken csrf(CsrfToken token) {
		return token;
	}
}
