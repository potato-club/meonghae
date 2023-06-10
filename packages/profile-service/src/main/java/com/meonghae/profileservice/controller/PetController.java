package com.meonghae.profileservice.controller;

import com.meonghae.profileservice.dto.pet.PetDetaileResponseDTO;
import com.meonghae.profileservice.dto.pet.PetInfoRequestDto;
import com.meonghae.profileservice.dto.pet.PetInfoResponseDTO;
import com.meonghae.profileservice.service.PetService;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;


@RestController
@RequestMapping("/profile")
@Api(value = "Pet Controller", tags = "반려동물 관련 서비스 API")
@RequiredArgsConstructor
public class PetController {
  private final PetService petService;
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
//  @ApiIgnore
//  @PostMapping
//  public String add(
//          @RequestPart List<MultipartFile> images,
//          @RequestPart PetInfoRequestDto petDTO,
//          @RequestHeader("Authorization") String token) {
//
//    return petService.savePet(images, petDTO, token);
//  }

  @Operation(summary = "반려동물 리스트 추가 [ 3마리까지만 테스트 부탁 ]")
  @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public String addPetList(@Parameter(name = "List<MultipartFile> images", description = "첨부파일리스트", required = true)
                             @RequestPart List<MultipartFile> images,
                           @Parameter(name = "List<PetInfoRequestDto> \"[ petName : (String) \"petGender : (BOY or GIRL) \"petBirth : (2022-01-01) \"petSpecies[동물 종] : (String) \"meetRoute : (String) ",
                                   required = true)
                            @RequestPart List<PetInfoRequestDto> petListDto,
                           @ApiParam(value = "사용자 토큰", required = true) @RequestHeader("Authorization") String token){

    return petService.savePetList(images, petListDto, token);
  }

  @Operation(summary = "반려동물 수정")
  @PutMapping("/{id}")
  public String update(
          @ApiParam(value = "반려동물 id", required = true)@PathVariable Long id,
          @ApiParam(value = "반려동물 사진", required = true)@RequestPart MultipartFile image,
          @ApiParam(value = "반려동물 정보", required = true) @RequestPart PetInfoRequestDto petDto) {

    return petService.update(id, image, petDto);
  }
  @Operation(summary = "반려동물 삭제")
  @DeleteMapping("/{id}")
  public String deleteById(@PathVariable Long id) {
    return petService.deleteById(id);
  }


}
