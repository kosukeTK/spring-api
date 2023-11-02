package com.example.demo.repository.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class TblMUserEntity implements Serializable  {
	String idUser;
	String username;
	String password;
}
