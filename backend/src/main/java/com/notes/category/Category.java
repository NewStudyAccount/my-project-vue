package com.notes.category;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类实体
 */
@Data
@TableName("categories")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String color;

    private Long userId;

    private LocalDateTime createdAt;
}
