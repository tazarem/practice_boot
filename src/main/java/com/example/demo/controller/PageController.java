package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.service.MainService;

@Controller
public class PageController { //페이지 컨트롤러

	@RequestMapping(value="/", method=RequestMethod.GET)
	public String homePage(Model model) {
		model.addAttribute("server_side_param","im server side param");
		return "index";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String loginPage() { //페이지 렌더링 컨트롤러
		return "login";
	}
	
	@RequestMapping(value="/join", method=RequestMethod.GET)
	public String joinPage() { //페이지 렌더링 컨트롤러
		return "join";
	}
	
	@RequestMapping(value="/test", method=RequestMethod.GET)
	public String testPage() { //페이지 렌더링 컨트롤러
		return "test";
	}

}
