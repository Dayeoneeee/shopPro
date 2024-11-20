package com.example.shoppro.controller;

import com.example.shoppro.dto.MemberDTO;
import com.example.shoppro.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
@Log4j2

public class MemberController {

    private final MemberService memberService;

    //회원가입
    @GetMapping("/new")
    public String memberForm(Model model){

        model.addAttribute("memberDTO", new MemberDTO());

        return "member/memberForm";

    }

    @PostMapping("/new")
    public String memberFormPost(@Valid MemberDTO memberDTO,
                                 BindingResult bindingResult, Model model){
        //입력을 받을 때 유효성 검사를 할 것이며,   //vailde

        log.info("저장의 post로 들어온 memeberDTO :" +memberDTO);
        if (bindingResult.hasErrors()){
            //유효성 검사에 이상이 있다면 다시 회원가입 페이지로 보낼 것이고,
            // 무엇을 가지고? = 에러 내용을. 그건 어디있니? = 자동으로 넘어간다.
            // 단, return으로 redirect 안됨. 그건 RedirectAttributes에 따로 담아야함
            // 에러는 무엇인가? 에 대한 로그
            log.info(bindingResult.getAllErrors());
            //이 에러를 가져오는 getAllErrors의 내용을 redirect로 보낼 때 가져가면 된다.

            return "member/memberForm";

        }

        //유효성 검사에 이상이 없다면 저장
        try {
            memberService.saveMember(memberDTO);
        }catch (IllegalStateException e){
            model.addAttribute("msg", e.getMessage());
            return "member/memberForm";

        }

        //저장 후 특정페이지로 이동하게 만들 것 이다.
        return null;

    }

    @GetMapping("/login")
    public String loginMember(){
        return "/member/loginForm";
    }







}
