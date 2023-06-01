package com.meonghae.communityservice.Client;

import com.meonghae.communityservice.Config.FeignHeaderConfig;
import com.meonghae.communityservice.Dto.S3Dto.S3RequestDto;
import com.meonghae.communityservice.Dto.S3Dto.S3ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "s3-file-service", configuration = {FeignHeaderConfig.class})
public interface S3ServiceClient {
    @GetMapping("/files")
    List<S3ResponseDto> getImages(S3RequestDto requestDto);

    @PostMapping("/files")
    ResponseEntity<String> uploadImage(@RequestPart List<MultipartFile> images, @RequestPart S3RequestDto requestDto);

    // 이미지 업데이트는 deleted << 이거 물어보고 만들기
}
