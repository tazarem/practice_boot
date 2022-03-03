package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.repository.UserRepository;
import com.example.demo.util.KeyMaker;
import com.example.demo.vo.PagingVo;
import com.example.demo.vo.UserVo;

@Service
public class UserService {

	@Autowired
	UserRepository dao;
	
	@Resource
	private JavaMailSender mailer;
	
	@Value("${serverdomain}")
	private String SERVER_DOMAIN;
	
	public Map<String,Object> joinUser(UserVo new_user) {

		Map<String,Object> resultMap = new HashMap<String, Object>();
		
		String alert_text = "회원가입에 실패하였습니다.";

		//패스워드 암호화하기
		String rawPassword = new_user.getUser_password();
		
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		
		String encodedPassword = bcrypt.encode(rawPassword);
		new_user.setUser_password(encodedPassword);

		//pk 생성
		KeyMaker km = new KeyMaker();
		
		final int CODE_LENGTH = 10;
		int record_count = dao.countUser(); 
		String pk = km.makePk(record_count, CODE_LENGTH);
		new_user.setUser_key(pk);
		
		//db insert
		int result = dao.joinUser(new_user);
		
		if(result==0) {
		}else {
			alert_text = "회원가입 완료. 로그인해 주세요.";
		}
		
		resultMap.put("result", result);
		resultMap.put("alert_text", alert_text);
		
		return resultMap;
	}
	
	public int checkId(String user_id) {
		int result = 0;
		
		result = dao.checkId(user_id);
		
	return result;}
	
	public UserVo loginUser(UserVo login_user) {
		String rawPassword = login_user.getUser_password();
		
		UserVo will_login_user = dao.login(login_user.getUser_id());
		
		System.out.println(login_user);
		
		if(will_login_user!=null) {//유저가 있을 경우
			String encodedPassword = will_login_user.getUser_password();
			
			BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
			
			boolean isCorrect = bcrypt.matches(rawPassword, encodedPassword);
			
			int isBan = will_login_user.getIs_ban();
			if(isBan==0) { //밴 처리가 안 되었을 때에만
				if(isCorrect) { //로그인 성공
					will_login_user.set_login(isCorrect);
				}else {//로그인 실패
					will_login_user.set_login(false);
					will_login_user.setReason("계정 아이디 또는 비밀번호가 올바르지 않습니다.");	
				}
			}else { //밴 처리가 된 유저
				will_login_user.set_login(false);
				will_login_user.setReason("밴 처리된 계정입니다.");	
			}
		}else { //없는 아이디입니다
			will_login_user = new UserVo();
			will_login_user.set_login(false);
			will_login_user.setReason("존재하지 않는 계정입니다.");	
		}		
		return will_login_user;
	} 
	
	public PagingVo getUserList(PagingVo page){ //유저 관리목록 가져오기
		List<UserVo> user_list = dao.getUserList(page);
		page.setValue(user_list);
		
		return page;
	}
	
	public int updateProfile(String saved_file_name,String user_key) {
		Map<String,String> queryMap = new HashMap<String,String>();
		queryMap.put("saved_file_name", saved_file_name);
		queryMap.put("user_key",user_key);
		
		int result = dao.updateProfile(queryMap);
		
	return result;}

	public UserVo getUserInfo(String user_key) {
		
		UserVo user_info = dao.getUserInfo(user_key);
		
		return user_info;
	}
	

	public int sendAuthMail(String reciever,String user_key, String user_id){
		try {	
			//auth_code 생성 및 DB 인서트
			//pk 생성
			KeyMaker km = new KeyMaker();
			
			String raw_auth_code = km.getRandomStr(4);
			System.out.println(raw_auth_code);
			
			BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
			
			String encoded_auth_code = bcrypt.encode(raw_auth_code);
			
			//이거 디비에 사용자 아이디랑 같이 묶어라
			Map<String,String> auth_param = new HashMap<String,String>();
			auth_param.put("user_key",user_key);
			auth_param.put("auth_key", encoded_auth_code);
			
			//인서트 하면 시퀀스 가져와서 넣기
			int result = dao.addAuthRecord(auth_param);
			int result2=dao.expireAllAuth(user_key);
			int result3=dao.activeLastAuth(user_key);	
			
			if(result==1) {
				System.out.println("중복인증 무효화:"+result2);
				System.out.println("마지막 인증요청 활성화:"+result3);
			}else {
				System.out.println("DB insert failed.");
				throw new MessagingException();
			}
			
			MimeMessage message = mailer.createMimeMessage(); //html 적용을 위해 마임 메세지 만들기
			
			String subject="[Partynote]이메일 인증 요청입니다.";
			System.out.println(reciever);
			
			//수신인 설정
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(reciever));
			//메일 제목
			message.setSubject(subject);
			//메일 내용
			String dom = "<a href='"+SERVER_DOMAIN+":8080/user/certEmail?auth_code="+raw_auth_code+"&user_id="+user_id+"'>이메일 인증하기</a>";
			message.setText(dom,"UTF-8","html");
			
			//메일 발송
			mailer.send(message);
			return 1; //성공
		
		}catch(MessagingException err) {
			System.out.println(err);
			return 0; //메일전송 실패
		}
		
	}
	
	public Map<String,Object> checkAuthMail(String auth_code, String user_id) {
		//리턴 객체 선언
		Map<String,Object> answer= new HashMap<String,Object>();
		
		try {
		//쿼리 결과값(베이스 가공자)
		Map result_pack= (HashMap<String,String>) dao.getUserAuthRecord(user_id);
		System.out.println(result_pack);
		
		if(result_pack!=null) {
			//만료시간 만들어서 비교하기
			Date auth_start = (Date)result_pack.get("auth_start");
			
			long auth_start_ms = auth_start.getTime();
			
			final long EXPIRE_MS = 1800000; //인증만료시간 : 30분
			
			long auth_end_ms = auth_start_ms+EXPIRE_MS;
			
			//지금시간 가져오기. 마이크로세컨드 형태로 비교
			Date now = new Date();
			long now_ms = now.getTime();

			if(auth_end_ms<now_ms) {//시간이 만료됨..
				throw new Exception("인증 시간 만료됨");
			}else {
				//조인한 테이블에 암호화한 코드를 비교매칭
				BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
				String encoded_auth_key = (String) result_pack.get("auth_key");
				boolean match=bcrypt.matches(auth_code, encoded_auth_key);
				
				//매칭되는가?
				if(match) {
					//업데이트 처리해주기
					String user_key = (String)result_pack.get("user_key");
					int confirm_result = dao.confirmCert(user_key);
					
					if(confirm_result==1) {
						//인증된 레코드는 만료 처리.
						String auth_key = (String)result_pack.get("auth_key");
						int expired_result = dao.confirmExpired(auth_key);
					}
					
					answer.put("alert_msg","인증되었습니다.");
					
				}else {
					throw new Exception("입력된 인증코드가 올바르지 않음");
				}
			}
		}else {
			throw new Exception("getUserAuthRecord 셀렉트 구문에 매칭되는 레코드가 없음");
		}
		}catch(Exception e) {
			System.out.println(e);
			answer.put("alert_msg","인증 시간이 만료되었거나 유효하지 않습니다.");
		}finally {
			//익스파이어드 처리하고 안된애들 레코드들 삭제하기
			int clean_result = dao.cleanExpired();
			System.out.println("만료된 레코드 삭제.. :"+clean_result);
		}
		return answer;
	}
	
}
