package org.example.apis;

import org.example.entity.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user-service")
public interface UserFeignApi {
    @GetMapping(value = "/douyin/user/list")
    List<UserInfoDTO> userInfoList(@RequestParam("user_id") List<Long> ids);
}
