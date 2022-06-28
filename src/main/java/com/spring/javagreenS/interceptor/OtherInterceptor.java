package com.spring.javagreenS.interceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class OtherInterceptor extends HandlerInterceptorAdapter {
	
	@Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
  	HttpSession session = request.getSession();
  	int level = session.getAttribute("sLevel")==null ? 99 : (int) session.getAttribute("sLevel");
  	if(level > 3) {			// 3:정회원 , 4:준회원, 99:비회원
  		RequestDispatcher dispatcher;
  		if(level == 99) {	// 비로그인자 사용불가
  			dispatcher = request.getRequestDispatcher("/msg/lelvelMemberNo");
  		}
  		else {	// level이 4(준회원) 사용불가
  			dispatcher = request.getRequestDispatcher("/msg/lelvelConfirmNo");
  		}
  		dispatcher.forward(request, response);
  		return false;
  	}
  	
  	return true;
  }
}
