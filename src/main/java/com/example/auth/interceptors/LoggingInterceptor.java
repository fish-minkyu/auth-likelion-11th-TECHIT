package com.example.auth.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;

// 들어오는 요청에 대해 어떤 헤더가 있었는지 로그로 남기기
@Slf4j
@Component // Bean 객체로 등록
public class LoggingInterceptor implements HandlerInterceptor { // HandlerInterceptor는 기본구현 방식이 있다.(default)

  // 요청이 HandlerMethod(Request Mapping) 메서드에 도달하기 전 실행
  @Override
  public boolean preHandle(
    // 요청 객체
    HttpServletRequest request,
    // 응답 객체
    HttpServletResponse response,
    // 실제로 요청을 처리할 RequestMapping을 나타내는 메서드 객체
    Object handler
  ) throws Exception {
    HandlerMethod handlerMethod = (HandlerMethod) handler;
    log.info("pre handling of {}", handlerMethod.getMethod().getName());
    // Enumeration
    // : 나열된 데이터를 나타내기위한 인터페이스
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      log.info("{}: {}", headerName, request.getHeader(headerName));
    }
    // preHandler이 false를 반환하면
    // 요청이 HandlerMethod로 전달되지 않음
    return true;
  }

  // HandlerMethod(RequestMapping)이 처리가 되고 응답이 보내지기 전 실행
  @Override
  public void postHandle(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler,
    ModelAndView modelAndView // view를 사용하면 view에다가 추가적인 정보를 제공할 때 사용하는 인자
  ) throws Exception {
    HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
  }

  // 요청 or 응답의 처리가 완전히 마무리 되었을 때 실행
  // 요청 처리 과정에서 예외가 발생하면 인자로 전달 받음
  @Override
  public void afterCompletion(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler,
    Exception ex // 예외가 발생하면 인자로 전달 받음
  ) throws Exception {
    HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
  }
}