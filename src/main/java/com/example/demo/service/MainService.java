package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.MainRepository;
import com.example.demo.vo.UserVo;

@Service
public class MainService {

	@Autowired
	private MainRepository dao;

	public List<UserVo> getUsers() {
		List<UserVo> users = dao.getUsers();
		return users;}

}
