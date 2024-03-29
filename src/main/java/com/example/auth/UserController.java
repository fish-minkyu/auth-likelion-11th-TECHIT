package com.example.auth;

import com.example.auth.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  private final UserDetailsManager manager; // JpaUserDetailsManager (다형성으로 주입 가능)
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationFacade authFacade;
  // interface 기반 DI (Strategy Pattern)
  // private final  IUserService service;

  @GetMapping("/home")
  public String home() {
    //NOTE Spring에서 실행하고 있는 코드 내부에서,
    // 현재 요청에 대해서 누가 접속해서 요청하고 있는지 파악 가능
    log.info(SecurityContextHolder.getContext().getAuthentication() // 로그인을 안해도 오류가 나지 않음
      .getName());
    // Facade Pattern
    log.info(authFacade.getAuth().getName());

    return "index";
  }

   // 로그인 화면 (로그인 하기 전에만 볼 수 있음)
   @GetMapping("/login")
   public String loginForm() {
     return "login-form";
   }

   // 로그인 한 후, 내가 누군지 확인하는 페이지 (로그인 하고 난 다음 볼 수 있음)
   @GetMapping("/my-profile")
  public String myProfile(
    //NOTE Authentication authentication
    // : Spring Request Mapping Handler가 지원하는 메서드 중 하나,
    // 메서드에 도달할 때 필요로하는 매개변수 타입을 Spring Boot가 알아서 넣어준다는 뜻
    // => 여기선 누가 접속했는지 알려준다.
    Authentication authentication
   ) {
     log.info(authentication.getName()); // 사용자 이름 출력
     // getPrincipal
     // : UserDetailsManager에서 사용하고 있는 User 객체 정보를 Principal에 담겨 있다.
//     log.info(((User) authentication.getPrincipal()).getPassword()); // null이 왜 나오지?
     log.info(((CustomUserDetails) authentication.getPrincipal()).getPassword());
     log.info(((CustomUserDetails) authentication.getPrincipal()).getEmail());
     return "my-profile";
   }

   // 회원가입 화면
   @GetMapping("/register")
   public String signUpForm() {
     return "register-form";
   }

   @PostMapping("/register")
   public String signUpRequest(
     @RequestParam("username") String username,
     @RequestParam("password") String password,
     @RequestParam("password-check") String passwordCheck
   ) {
     //Note 추후 비밀번호 일치여부와 암호화는 Service 계층으로 옮겨주자
     // TODO password == passwordCheck
     if (password.equals(passwordCheck)) {
       // TODO 주어진 정보를 바탕으로 새로운 사용자 생성

       // UserDetails 사용
     /*  manager.createUser(User.withUsername(username)
         .password(passwordEncoder.encode(password)) // 비밀번호 암호
         .build());
     */

       // CustomUserDetails 사용해서 보내주기
       manager.createUser(CustomUserDetails.builder()
           .username(username)
           .password(passwordEncoder.encode(password))
         .build());
     }

     // 회원가입 성공 후 로그인 페이지로
     return "redirect:/users/login";
   }
}
