package com.example.demo.repository;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.vo.UserVo;

@Repository
public class MainRepository {

	@Autowired
	SqlSessionTemplate sql;

	public List<UserVo> getUsers(){
		return sql.selectList("userMapper.selectUsers");
	}

	public int joinUser(UserVo new_user) {
		return sql.insert("userMapper.joinUser",new_user);
	}

}
