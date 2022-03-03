package com.example.demo.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.service.UserService;
import com.example.demo.vo.PagingVo;
import com.example.demo.vo.UserVo;

@Controller
@RequestMapping(value="/user")
public class UserController { //페이지 렌더링?

	@Autowired
	UserService service;

	@RequestMapping(value="/doJoin", method=RequestMethod.POST)
	public ModelAndView doJoin(@ModelAttribute UserVo newUser) { //회원가입
		ModelAndView mav;
		Map<String,Object> resultMap =service.joinUser(newUser);
		int result = (int)resultMap.get("result");
		String alert_text = (String)resultMap.get("alert_text");
		if(result==1) { //회원가입 성공
			mav = new ModelAndView("index");
			mav.addObject("alert_text", alert_text);
		}else{
			mav = new ModelAndView("index");
			mav.addObject("alert_text", alert_text);
		}
		
	return mav;}

	@ResponseBody
	@RequestMapping(value="/checkId", method=RequestMethod.GET) //아이작스는 아이작스.
	public int checkId(@RequestParam(required = false) String userId) { //아이디 중복확인
		int result = 1;	
		result = service.checkId(userId);
	return result;}

	@RequestMapping(value="/doLogin", method=RequestMethod.POST)
	public ModelAndView doLogin(HttpServletRequest request, @ModelAttribute UserVo user) { //로그인
		ModelAndView mav = new ModelAndView("index");
		
		System.out.println(user);
		
		UserVo will_login_user= service.loginUser(user);
		if(will_login_user.is_login()) { //로그인 성공
			HttpSession session = request.getSession();
			String alert_text = will_login_user.getUser_nickname()+"님 로그인 되었습니다.";
			mav.addObject("alert_text", alert_text);
			
			session.setAttribute("user_key", will_login_user.getUser_key());
			session.setAttribute("user_id", will_login_user.getUser_id());
			session.setAttribute("user_email", will_login_user.getUser_email());
			session.setAttribute("user_nickname", will_login_user.getUser_nickname());
			session.setAttribute("is_cert", will_login_user.getIs_cert());
			session.getCreationTime();
			
		}else { //로그인 실패 및 사유 스크립트 전송
			String alert_text = will_login_user.getReason();
			mav.addObject("alert_text", alert_text);
		}
		
	return mav;}
	
	@ResponseBody
	@RequestMapping(value="/doLogout", method=RequestMethod.GET)
	public int doLogout(HttpServletRequest request) {
		int result = 0;
		HttpSession session = request.getSession();
		session.invalidate();
		result=1;
	return result;}
	
	@RequestMapping(value="/getUserList", method=RequestMethod.GET)
	public ModelAndView getUserList(@ModelAttribute PagingVo page) {
		//page내부에서 offset과 limit를 세팅함.
		ModelAndView mav;
		PagingVo renderPage = service.getUserList(page);
		mav = new ModelAndView("user/user-list");
		mav.addObject("userList", renderPage.getValue());
		
	return mav;}
	
	@RequestMapping(value="/findPw", method=RequestMethod.POST) //이메일인증하는 시도를 해야지
	public void findPw() { //비밀번호 찾기

	}
	
	@RequestMapping(value="/certEmail", method=RequestMethod.GET)
	public ModelAndView certEmail(@RequestParam String auth_code, @RequestParam String user_id) {
		//코드가 만료되지 않은 것 중에서 auth_code를 찾고 매칭하기
		//날짜 체크해서 지낫으면 비활성 처리
		
		Map answer=service.checkAuthMail(auth_code,user_id);
		
		ModelAndView mav = new ModelAndView("certmail");
		mav.addObject("alert_msg",answer.get("alert_msg"));
		
		return mav;
	}
}
