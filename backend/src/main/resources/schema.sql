-- 创建数据库
CREATE DATABASE IF NOT EXISTS notes_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE notes_db;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password   VARCHAR(255) NOT NULL COMMENT 'BCrypt 加密密码',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL COMMENT '分类名称',
    color      VARCHAR(20)  DEFAULT '#409EFF' COMMENT '颜色值',
    user_id    BIGINT       NOT NULL COMMENT '所属用户',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 笔记表
CREATE TABLE IF NOT EXISTS notes (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200) NOT NULL COMMENT '标题',
    content     TEXT COMMENT '内容',
    category_id BIGINT COMMENT '分类 ID，可为空',
    user_id     BIGINT       NOT NULL COMMENT '所属用户',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记表';
