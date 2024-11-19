package com.example.shoppro.config;

import jakarta.servlet.annotation.WebListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@WebListener
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{

        httpSecurity

                // 권한. 페이지 접속권한.
                .authorizeHttpRequests(
                        authorization -> authorization
                                .requestMatchers("/user/login/**").permitAll()  //로그인페이지는 누구나 접속이 가능한 권한
                                .requestMatchers("/board/register").authenticated()  //로그인 한 사람만 접속 가능
                                .requestMatchers("/item/register").hasRole("ADMIN")  //사장님만 아이템 등록 가능
                                .requestMatchers("/user/list").hasRole("ADMIN")  //사장님만 유저목록 확인 가능
                                .anyRequest().permitAll()                           //이 외는 모두 허용
//                                    .anyRequest().authenticated()                     //이 외에는 다 로그인해서 접속해
                )
                // 위변조 방지. 웹에서 form 태그 변경 등의 변조를 방지
                .csrf(csrf -> csrf.disable())
                //로그인
                .formLogin(
                        formLogin  -> formLogin.loginPage("/user/login")        //기본 로그인 페이지 지정
                                .defaultSuccessUrl("/user/login")                         //로그인이 성공했다면
                                .usernameParameter("email")                     //로그인 <input name="email">
                        //컨트롤러로 보낼때~ 기본페이지 지정
                )
                // 로그아웃
                .logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))       //로그아웃 a태그라고 생각
                                //<a href="/user/logout">로그아웃</a>
                                .invalidateHttpSession(true)                //세션 초기화
                                .logoutSuccessUrl("/")                      //localhost:8090으로 간다.
                        //dns주소 일 경우 : www.naver.com까지 로 간다.
                        //컨트롤러에서 만들어 줄 것이다.
                )

        //예외처리//로그인이되지않은 사용자, 권한이 없는 사용자가 접속시 취할 행동들
//                .exceptionHandling(
//                        a -> a
//                )

        ;
        return httpSecurity.build();
    }


    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



}
