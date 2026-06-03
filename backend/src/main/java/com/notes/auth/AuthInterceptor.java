package com.notes.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notes.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器：校验 JWT token
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenUtil tokenUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeError(response, 401, "未登录，请先登录");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            Long userId = tokenUtil.getUserId(token);
            String username = tokenUtil.getUsername(token);
            UserContext.setCurrentUserId(userId);
            UserContext.setCurrentUsername(username);
            return true;
        } catch (Exception e) {
            log.warn("token 解析失败: {}", e.getMessage());
            writeError(response, 401, "token 无效或已过期，请重新登录");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<?> result = Result.error(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
