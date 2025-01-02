package com.example.maraon.controller;

import com.example.maraon.dto.UserRequestDTO;
import com.example.maraon.dto.UserResponseDTO;
import com.example.maraon.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입: 페이지 응답
    @GetMapping("/user/join")
    public String joinPage() {
        return "join";
    }

    // 회원 가입: 수행
    @PostMapping("/user/join")
    public String joinProcess(UserRequestDTO dto) {

        userService.createOneUser(dto);

        return "redirect:/login";
    }

    // 회원 수정: 페이지 응답
    @GetMapping("/user/update/{email}")
    public String updatePage(@PathVariable("email") String email, Model model) {

        // 본인 또는 ADMIN 권한만 접근 가능
        if (userService.isAccess(email)) {
            UserResponseDTO dto = userService.readOneUser(email);
            model.addAttribute("USER", dto);
            return "update";
        }

        return "redirect:/login";

    }

    // 회원 수정: 수행
    @PostMapping("/user/update/{email}")
    public String updateProcess(@PathVariable String email, UserRequestDTO dto) {

        // 본인 또는 ADMIN 권한만 접근 가능
        if (userService.isAccess(email)) {
            userService.updateOneUser(dto, email);
        }

        return "redirect:/user/update/" + email;
    }

}
