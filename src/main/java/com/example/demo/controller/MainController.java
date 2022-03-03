package com.example.demo.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
	
	@RequestMapping(value="/filtering", method=RequestMethod.POST)
	public ModelAndView doJoin(@RequestParam(required=false) String inner_text, @RequestParam(required=false) String inner_script) { //회원가입
		ModelAndView mav = new ModelAndView("test");
		
		System.out.println(inner_text);
		
		mav.addObject("inner_text", inner_text);
		mav.addObject("inner_script", inner_script);

	return mav;}

}
