package com.example.demo.util;

import java.util.List;
import java.util.Map;

public class JsonMapper {
	private Map<String,Object> json_map;
	
	public JsonMapper(Map<String,Object> json_map) {
		this.json_map = json_map;
	}
	//커스텀 제이슨 매퍼
	//생성자: string, object
	//리스트면 리스트로 반환
	//문자열이면 문자열로 반환
	//객체면 객체 반환
	
	public void setInner(Map<String,Object> json_map) {
		this.json_map = json_map;	
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getList(String keyName){
	return (List<Object>)json_map.get(keyName);
	}
	
	public Object getObject(String keyName){	
	return json_map.get(keyName);
	}
	
	public String getString(String keyName){	
	return (String)json_map.get(keyName);
	}
	
}
