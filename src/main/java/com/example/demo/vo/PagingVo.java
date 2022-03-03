package com.example.demo.vo;

import java.util.List;

import lombok.Data;

@Data
public class PagingVo {
	
	private int offset;
	private int limit;
	private int start;
	private int end;
	private List<?> value;
	
}
