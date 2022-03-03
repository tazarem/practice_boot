package com.example.demo.vo;

import lombok.Data;

@Data
public class UserVo {

	private String user_key;
	private String user_id;
	private String user_password;
	private String user_email;
	private String user_nickname;
	private String user_profile;

	private int is_cert;
	private int is_ban;
	private boolean is_login;

	private String reason;
	
	private String write_date;
	private String recent_date;

}
