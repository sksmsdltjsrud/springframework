package com.spring.javagreenS.vo;

import lombok.Data;

// lombok은 getter, setter이 만들어짐
public @Data class GuestVO {
	private int idx;
	private String name;
	private String email;
	private String homepage;
	private String vDate;
	private String hostIp;
	private String content;
}
