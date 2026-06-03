package com.notes.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.notes.common.BusinessException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户名查找用户
     */
    public User findByUsername(String username) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
    }

    /**
     * 根据 ID 查找用户
     */
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 注册
     */
    public void register(String username, String password) {
        // 检查用户名是否已存在
        User existing = findByUsername(username);
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        userMapper.insert(user);
    }

    /**
     * 登录验证：返回用户对象（密码校验失败则抛异常）
     */
    public User login(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return user;
    }
}
