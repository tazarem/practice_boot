package com.example.demo.controller;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.UserService;
import com.example.demo.util.JsonMapper;
import com.example.demo.vo.UserVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping (value="/api")
@CrossOrigin(origins={"${test.client}","${test.client2}"}, allowCredentials = "true" )
public class ApiController { //ajax 전용 컨트롤러
	
	static ObjectMapper mapper = new ObjectMapper(); //오브젝트 맵퍼
	static final String PROFILE_SAVE_PATH = "src\\main\\resources\\static\\profiles";
	
	@Resource
	UserService service;

	@RequestMapping(value="/echoJson", method=RequestMethod.POST) //아이작스는 아이작스.
	public Object echoJson(@RequestBody String raw_json) throws JsonMappingException, JsonProcessingException { //제이슨받기 테스트

		//ObjectMapper.readValue =>raw json 객체를 읽어서 java Object화한다. 왼측은 읽을 날것, 우측은 변환참조타입을 받는다.
		Map<String, Object> json_map = mapper.readValue(raw_json,
				new TypeReference<Map<String,Object>>(){});
		
		JsonMapper json = new JsonMapper(json_map); //타입 리퍼런스 : 즉석 클래스 생성처럼 씀.. 인라인 객체로 쓰려고 하는것임
		
		Map<String, Object> innerJson = (Map<String, Object>)json.getObject("json");
		
		json.setInner(innerJson);
		
		System.out.println(json.getList("some_arr"));
		
		return json_map;
	}
	
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public Map<String,Object> login(HttpServletRequest request, @ModelAttribute UserVo login_user) { //로그인
		
		//응답객체 형성
		Map<String,Object> answer = new HashMap<String,Object>();
		
		//서비스 로그인 로직
		UserVo will_login_user = service.loginUser(login_user);

		if(will_login_user.is_login()) {
			//세션 증여
			HttpSession session = request.getSession();
			
			session.setAttribute("user_key",will_login_user.getUser_key());
			session.setAttribute("user_id", will_login_user.getUser_id());
			session.setAttribute("user_email", will_login_user.getUser_email());
			session.setAttribute("user_profile", will_login_user.getUser_profile());
			session.setAttribute("user_nickname", will_login_user.getUser_nickname());
			session.setAttribute("is_cert", will_login_user.getIs_cert());
			session.getCreationTime();
			
			//성공 응답 작성
			answer.put("isLogin",true);
			
			Map<String,Object> userInfo = new HashMap<String,Object>();
			userInfo.put("user_id",will_login_user.getUser_id());
			userInfo.put("user_email",will_login_user.getUser_email());
			userInfo.put("user_profile",will_login_user.getUser_profile());
			userInfo.put("user_nickname",will_login_user.getUser_nickname());
			userInfo.put("is_cert",will_login_user.getIs_cert());

			answer.put("userInfo", userInfo);
			
		}else {
			//실패 응답 작성
			String alert_text = will_login_user.getReason();
			answer.put("isLogin",false);
			answer.put("alert_text",alert_text);
		}
		
	return answer;}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public Map<String,Object> logout(HttpServletRequest request) {
		//응답객체 형성
		Map<String,Object> answer = new HashMap<String,Object>();
		
		HttpSession session = request.getSession();
		
		session.invalidate();
		
		answer.put("answer", 1);
	return answer;}
	
	@RequestMapping(value="/sessionCheck", method=RequestMethod.GET)
	public Map<String,Object> sessionCheck(HttpServletRequest request) {//세션체크. 로그인상태로 전환해주기
		//응답객체 형성
		Map<String,Object> answer = new HashMap<String,Object>();
				
		//세션 확인
		HttpSession session = request.getSession();
		//null이라면 0반환
		Object userSession = session.getAttribute("user_key");
		
		System.out.println(userSession);
		
		if(userSession==null) {
			answer.put("isLogin", false);
		}else {
			//세션 있음
			Object userId = session.getAttribute("user_id");
			Object userEmail = session.getAttribute("user_email");
			Object userNickname = session.getAttribute("user_nickname");
			Object userProfile = session.getAttribute("user_profile");
			Object isCert = session.getAttribute("is_cert");
			
			Map<String,Object> userInfo = new HashMap<String,Object>();
			
			userInfo.put("user_id",userId);
			userInfo.put("user_email",userEmail);
			userInfo.put("user_nickname",userNickname);
			userInfo.put("user_profile",userProfile);
			userInfo.put("is_cert",isCert);

			answer.put("userInfo", userInfo);
			answer.put("isLogin", true);
		}
		
	 return answer;}
	
	@RequestMapping(value="/getUserInfo", method=RequestMethod.GET)
	public Map<String,Object> getUserInfo(HttpServletRequest request) { //유저 정보가 갱신되었을 때 DB에서 다시 긁어옴
		//응답객체 형성
		Map<String,Object> answer = new HashMap<String,Object>();
				
		//세션 확인
		HttpSession session = request.getSession();
		//null이라면 0반환
		Object userSession = session.getAttribute("user_key");
		if(userSession==null) {
			answer.put("isLogin", false);
		}else {
			String user_key = (String)userSession;
			UserVo user_info = service.getUserInfo(user_key);
			
			//세션정보도 업데이트
			session.setAttribute("user_id", user_info.getUser_id());
			session.setAttribute("user_email", user_info.getUser_email());
			session.setAttribute("user_nickname", user_info.getUser_nickname());
			session.setAttribute("user_profile", user_info.getUser_profile());
			session.setAttribute("is_cert", user_info.getIs_cert());
			
			answer.put("userInfo", user_info);
			answer.put("isLogin", true);
		}
		
	return answer;}
	
	@RequestMapping(value="/join", method=RequestMethod.GET)
	public Map<String,Object> join() {
		//응답객체 형성
		Map<String,Object> answer = new HashMap<String,Object>();
		
		//응답 작성
		answer.put("answer",1);
	return answer;}
	
	@RequestMapping(value="/uploadProfile", method=RequestMethod.POST)
	public Map<String,Object> uploadProfile(HttpServletRequest request, @RequestParam(name="file", required=false) MultipartFile file){ //다중일 경우 List<MultipartFile> 로 받기 프로파일 사진 업로딩 함수
			
		//응답객체 형성
		Map<String,Object> answer = new HashMap<String,Object>();
		
		//세션에서 유저 키 읽어서 유저 키 프로파일로 저장하기
		HttpSession session = request.getSession();
		String user_key = (String)session.getAttribute("user_key");
		System.out.println("프로파일 변경 : "+user_key);
		
		//실 이미지 파일 저장
		if(!file.isEmpty()&&user_key!=null) {
			
			String original_name = file.getOriginalFilename();
			String ext = original_name.substring(original_name.lastIndexOf(".") + 1);
			
			String saved_file_name = "profile_"+user_key+"."+ext;
			
			String src = PROFILE_SAVE_PATH+"\\"+saved_file_name;

			try {
				
				FileOutputStream output = new FileOutputStream(src);
				output.write(file.getBytes());
				output.close();
				System.out.println("파일 저장됨.");
				//응답 작성
				answer.put("file_save",1);
				
			}catch(Exception e) {
				e.getStackTrace();	
				//응답 작성
				answer.put("file_save",0);
			}
			
			//DB에 업데이트
			//saved_file_name, user_key
			int result = service.updateProfile(saved_file_name, user_key);
			//응답 작성
			answer.put("db_insert", result);
		}


	return answer;}
	
	@RequestMapping(value="/sendEmail", method=RequestMethod.GET) //승인되지 않은 유저일 경우 이메일 인증하기를 눌러 등록한 이메일로 인증메일이 발송되도록 한다.
	public Map<String,Object> sendEmail(HttpServletRequest request) { //DB에 인증코드 등록하고 이메일로 발송해서 승인 유도하기
		System.out.println("이메일 송부요청 도착");
		//응답객체 형성
		Map<String,Object> answer = new HashMap<String,Object>();
		
		//세션 추출
		HttpSession session =  request.getSession();
		String user_email = (String)session.getAttribute("user_email");
		String user_key = (String)session.getAttribute("user_key");
		String user_id = (String)session.getAttribute("user_id");
		
		int result=0;
		
		if(user_email!=null) {
			result = service.sendAuthMail(user_email,user_key,user_id);
		}
		
		answer.put("result", result);
//		boolean isCorrect = bcrypt.matches(rawPassword, encodedPassword);
		
	return answer;}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
