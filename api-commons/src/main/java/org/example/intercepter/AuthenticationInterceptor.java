package org.example.intercepter;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserDTO;
import org.example.utils.JwtUtil;
import org.example.utils.UserHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头中的token
        String token = request.getParameter("token");
        if (StrUtil.isBlank(token)) {
            return false;
        }
        UserDTO userDTO = JwtUtil.parseToken(token);
        log.info("AuthenticationInterceptor preHandle {} {}", userDTO.getId(), userDTO.getUsername());
        // 保存用户信息到 ThreadLocal
        UserHolder.saveUser(userDTO);
        // 8.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserHolder.removeUser();
    }
    //    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String token = request.getHeader("access-token");
//        if (token == null || token.isEmpty()) {
//            return false;
//        }
//        JwtUtil.parseToken(token);
//        return true;
//    }
}
