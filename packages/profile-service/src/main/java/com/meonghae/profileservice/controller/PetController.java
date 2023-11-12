package com.meonghae.profileservice.controller;

import com.meonghae.profileservice.dto.pet.PetDetaileResponseDTO;
import com.meonghae.profileservice.dto.pet.PetInfoRequestDto;
import com.meonghae.profileservice.dto.pet.PetInfoResponseDTO;
import com.meonghae.profileservice.service.PetService;

import java.util.List;

import com.meonghae.profileservice.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
//
import javax.servlet.http.HttpServletRequest;




@RestController
@RequestMapping("/profile")
@Api(value = "Pet Controller", tags = "반려동물 관련 서비스 API")
@RequiredArgsConstructor
@Slf4j
public class PetController {
  private final PetService petService;
  private final RedisService redisService;
  @Value("${subnet.allowed}")
  private String allowedSubnet;

  @Operation(summary = "유저의 반려동물 리스트")
  @GetMapping // user의 반려동물 리스트
  public List<PetInfoResponseDTO> getUserPetList(@ApiParam(value = "사용자 토큰", required = true) @RequestHeader("Authorization") String token) {
    return petService.getUserPetList(token);
  }
  @Operation(summary = "한 마리의 반려동물 정보")
  @GetMapping("/{id}") // 하나의 반려동물
  public PetDetaileResponseDTO getUserPet(@ApiParam(value = "반려동물 id", required = true) @PathVariable Long id) {
    return petService.getOneOfPet(id);
  }
  @Operation(summary = "전체 반려동물 세부 정보")
  @GetMapping("/all")
  public List<PetDetaileResponseDTO> getAllUserPet(@RequestHeader("Authorization") String token){
    return petService.getAllPet(token);
  }

  @Operation(summary = "반려동물 단일 추가")
  @PostMapping("")
  public String addPetList( PetInfoRequestDto petDto,
                           @ApiParam(value = "사용자 토큰", required = true) @RequestHeader("Authorization") String token){

    return petService.savePetList(petDto, token);
  }

  @Operation(summary = "반려동물 수정")
  @PutMapping("/{id}")
  public String update(
          @ApiParam(value = "반려동물 id", required = true)@PathVariable Long id,
          PetInfoRequestDto petDto) {

    return petService.update(id, petDto);
  }
  @Operation(summary = "반려동물 삭제")
  @DeleteMapping("/{id}")
  public String deleteById(@PathVariable Long id) {
    return petService.deleteById(id);
  }

  @Operation(summary = "Feign용 이메일로 데이터삭제")
  @DeleteMapping("/users")
  public void deletedByUserEmail(@RequestPart String userEmail, HttpServletRequest request){
    if (!isIpInSubnet(request.getRemoteAddr(), allowedSubnet)) {
      throw new RuntimeException();
    }
    log.info("******** = " + request.getRemoteAddr());
    //petService.deleteByUserEmail(userEmail);
  }
  private boolean isIpInSubnet(String ipAddress, String subnet) {
    if (ipAddress.contains(":")) {
      return false; // IPv6 주소는 거부
    }
    SubnetUtils utils = new SubnetUtils(subnet);
    return utils.getInfo().isInRange(ipAddress);
  }
  @Operation(summary = "Feign용 Fcm토큰 변경시 사용")
  @PostMapping("/exchange/token")
  public void getReviseFcmToken(@RequestPart String email, @RequestPart String fcmToken) {
    redisService.updateFcm(email,fcmToken);
  }
}
