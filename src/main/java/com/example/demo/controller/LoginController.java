package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class LoginController {

	@GetMapping("/login/saml2/sso/one")
	public String index(Model model, @AuthenticationPrincipal Saml2AuthenticatedPrincipal principal) {
		String emailAddress = principal.getFirstAttribute("email");
		model.addAttribute("emailAddress", emailAddress);
		model.addAttribute("userAttributes", principal.getAttributes());
		return "index";
	}
	
	@GetMapping("/fail")
	public ResponseEntity<Map<String, Object>> home() {
		Map<String, Object> resultMap = new HashMap<>();
		System.out.println("home");
		return new ResponseEntity<Map<String, Object>> (resultMap, HttpStatus.FORBIDDEN);
	}
	
	@PostMapping("/todo")
	public Map<String, Object> getMenu() {
		Map<String, Object> resultMap = new HashMap<>();
		System.out.println("todo");
		return resultMap;
	}
}
