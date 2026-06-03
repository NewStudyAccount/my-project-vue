package com.notes.auth;

import com.notes.common.Result;
import com.notes.user.User;
import com.notes.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口：注册、登录、获取当前用户
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenUtil tokenUtil;

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.isBlank()) {
            return Result.error(400, "用户名不能为空");
        }
        if (password == null || password.isBlank()) {
            return Result.error(400, "密码不能为空");
        }
        if (username.length() < 3 || username.length() > 20) {
            return Result.error(400, "用户名长度为 3-20 位");
        }
        if (password.length() < 6 || password.length() > 20) {
            return Result.error(400, "密码长度为 6-20 位");
        }

        userService.register(username, password);
        return Result.ok("注册成功");
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.isBlank()) {
            return Result.error(400, "用户名不能为空");
        }
        if (password == null || password.isBlank()) {
            return Result.error(400, "密码不能为空");
        }

        User user = userService.login(username, password);

        // 生成 token
        String token = tokenUtil.generateToken(user.getId(), user.getUsername());

        // 构造返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId().toString());
        userInfo.put("username", user.getUsername());
        data.put("user", userInfo);

        return Result.ok(data);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<?> me() {
        Long userId = UserContext.getCurrentUserId();
        User user = userService.findById(userId);
        if (user == null) {
            return Result.error(401, "用户不存在");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId().toString());
        userInfo.put("username", user.getUsername());
        return Result.ok(userInfo);
    }
}
