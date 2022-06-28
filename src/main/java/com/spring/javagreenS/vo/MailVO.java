package com.spring.javagreenS.vo;

import lombok.Data;

// 여기에 @data 해도 됨
public @Data class MailVO {
	private String toMail;
	private String title;
	private String content;
}
