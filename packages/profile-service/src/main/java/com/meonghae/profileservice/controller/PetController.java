package com.meonghae.profileservice.controller;

import com.meonghae.profileservice.dto.PetInfoRequestDto;
import com.meonghae.profileservice.dto.PetInfoResponseDTO;
import com.meonghae.profileservice.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * user -> profile 요청시 펫 id와 이름 list 리턴
 *  -> 추가, 삭제, 업데이트
 */
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;
    @Operation(summary = "유저의 반려동물 리스트")
    @GetMapping // uset의 반려동물 리스트
    public List<PetInfoResponseDTO> getUserPetList(HttpServletRequest request){
        return petService.getUserPetList(request);
    }
    @Operation(summary = "한 마리의 반려동물 정보")
    @GetMapping("/{id}") // 하나의 반려동물
    public PetInfoResponseDTO getUserPet(@PathVariable Long id,HttpServletRequest request){
        return petService.getUserPet(id,request);
    }
    @Operation(summary = "반려동물 추가")
    @PostMapping
    public String add(@RequestBody PetInfoRequestDto petDTO, HttpServletRequest request) {
        return petService.save(petDTO, request);
    }
    @Operation(summary = "반려동물 수정")
    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody PetInfoRequestDto petDTO,HttpServletRequest request) {
        return petService.update(id, petDTO,request);
    }
    @Operation(summary = "반려동물 삭제")
    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id, HttpServletRequest request) {
        return petService.deleteById(id,request);
    }
}
