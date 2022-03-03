package com.example.demo.repository;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.vo.PagingVo;
import com.example.demo.vo.UserVo;

import lombok.extern.slf4j.Slf4j;

@Repository
public class UserRepository {

	@Autowired
	SqlSessionTemplate sql;

	public int joinUser(UserVo newUser){
		return sql.insert("userMapper.joinUser",newUser);
	}

	public List<UserVo> getUserList(PagingVo page) {
		// TODO Auto-generated method stub
		return sql.selectList("userMapper.getUserList",page);
	}

	public UserVo login(String user_id) {
		return sql.selectOne("userMapper.loginUser",user_id);
	}

	public int checkId(String user_id) {
		return sql.selectOne("userMapper.checkId",user_id);
	}

	public int countUser() {
		// TODO Auto-generated method stub
		return sql.selectOne("userMapper.countUser");
	}

	public int updateProfile(Map<String, String> queryMap) {
		return sql.update("userMapper.updateProfile", queryMap);
	}

	public UserVo getUserInfo(String user_key) {
		// TODO Auto-generated method stub
		return sql.selectOne("userMapper.getUserInfo", user_key);
	}
	
	public int addAuthRecord(Map auth_param) {
	return sql.insert("userMapper.addAuthRecord", auth_param);}
	
	public int expireAllAuth(String user_key) {
		return sql.update("userMapper.expireAllAuth",user_key);
	}

	public int activeLastAuth(String user_key) {
		return sql.update("userMapper.activeLastAuth", user_key);
	}

	public Object getUserAuthRecord(String user_id) {
		// TODO Auto-generated method stub
		return sql.selectOne("userMapper.getUserAuthRecord", user_id);
	}

	public int confirmCert(String user_key) {
		// TODO Auto-generated method stub
		return sql.update("userMapper.confirmCert", user_key);
	}

	public int cleanExpired() {
		return sql.delete("userMapper.cleanExpired");
	}

	public int confirmExpired(String auth_key) {
		// TODO Auto-generated method stub
		return sql.update("userMapper.confirmCert", auth_key);
	}
	
}
