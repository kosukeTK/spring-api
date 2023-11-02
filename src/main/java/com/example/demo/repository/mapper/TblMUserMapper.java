package com.example.demo.repository.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.repository.entity.TblMUserEntity;

@Mapper
public interface TblMUserMapper {
	TblMUserEntity getUserName(String username);
}
