package com.meonghae.userservice.controller;

import com.meonghae.userservice.dto.UserMyPageDto;
import com.meonghae.userservice.dto.UserRequestDto;
import com.meonghae.userservice.dto.UserResponseDto;
import com.meonghae.userservice.repository.UserRepository;
import com.meonghae.userservice.service.Interface.UserService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@Api(value = "USER_CONTROLLER", tags = "User Service 컨트롤러")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Operation(summary = "Gateway 서비스 내 토큰 재발급 로직용 API")
    @GetMapping("/users/{email}")
    public String getUserRoles(@PathVariable String email) {
        return userRepository.findByEmail(email).get().getUserRole().toString();
    }

    @Operation(summary = "Feign Client 전송용 API - 이메일")
    @GetMapping("/send/email")
    public String sendEmail(@RequestHeader("Authorization") String token) {
        return userService.sendEmail(token);
    }

    @Operation(summary = "Feign Client 전송용 API - 닉네임")
    @GetMapping("/send/{email}")
    public String sendNickname(@PathVariable String email) {
        return userService.sendNickname(email);
    }

    @Operation(summary = "회원가입 API")
    @GetMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserRequestDto userDto) {
        userService.signUp(userDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @Operation(summary = "내 정보 확인 API")
    @GetMapping("/mypage")
    public UserMyPageDto viewMyPage(HttpServletRequest request) {
        return userService.viewMyPage(request);
    }

    @Operation(summary = "카카오 로그인 API")
    @GetMapping("/login")
    public UserResponseDto login(@RequestParam String email, HttpServletResponse response) {
        return userService.login(email, response);
    }

    @Operation(summary = "로그아웃 API")
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        userService.logout(request);
        return ResponseEntity.ok("로그아웃 되었습니다");
    }

    @Operation(summary = "회원정보 수정 API")
    @PutMapping("/")
    public ResponseEntity<String> updateNickname(@RequestBody UserRequestDto userDto, HttpServletRequest request) {
        userService.update(userDto, request);
        return ResponseEntity.ok("닉네임이 변경되었습니다.");
    }

    @Operation(summary = "회원탈퇴 API")
    @PutMapping("/withdrawal")
    public ResponseEntity<String> withdrawalMembership(HttpServletRequest request) {
        userService.withdrawalMembership(request);
        return ResponseEntity.ok("회원탈퇴 처리 되었습니다");
    }
}
