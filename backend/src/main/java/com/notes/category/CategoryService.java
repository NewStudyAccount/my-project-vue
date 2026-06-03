package com.notes.category;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.notes.auth.UserContext;
import com.notes.common.BusinessException;
import com.notes.note.Note;
import com.notes.note.NoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private NoteMapper noteMapper;

    /**
     * 获取当前用户的分类列表
     */
    public List<Map<String, Object>> listCategories() {
        Long userId = UserContext.getCurrentUserId();

        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getUserId, userId)
                        .orderByDesc(Category::getCreatedAt)
        );

        return categories.stream().map(c -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", c.getId().toString());
            item.put("name", c.getName());
            item.put("color", c.getColor());
            return item;
        }).collect(Collectors.toList());
    }

    /**
     * 创建分类
     */
    public Map<String, Object> createCategory(String name, String color) {
        Long userId = UserContext.getCurrentUserId();

        Category category = new Category();
        category.setName(name);
        category.setColor(color != null ? color : "#409EFF");
        category.setUserId(userId);
        categoryMapper.insert(category);

        Map<String, Object> data = new HashMap<>();
        data.put("id", category.getId().toString());
        data.put("name", category.getName());
        data.put("color", category.getColor());
        return data;
    }

    /**
     * 更新分类
     */
    public Map<String, Object> updateCategory(Long id, String name, String color) {
        Long userId = UserContext.getCurrentUserId();
        Category category = categoryMapper.selectById(id);
        if (category == null || !category.getUserId().equals(userId)) {
            throw new BusinessException("分类不存在");
        }

        category.setName(name);
        if (color != null) {
            category.setColor(color);
        }
        categoryMapper.updateById(category);

        Map<String, Object> data = new HashMap<>();
        data.put("id", category.getId().toString());
        data.put("name", category.getName());
        data.put("color", category.getColor());
        return data;
    }

    /**
     * 删除分类（级联清空关联笔记的 category_id）
     */
    @Transactional
    public void deleteCategory(Long id) {
        Long userId = UserContext.getCurrentUserId();
        Category category = categoryMapper.selectById(id);
        if (category == null || !category.getUserId().equals(userId)) {
            throw new BusinessException("分类不存在");
        }

        // 将该分类下所有笔记的 category_id 置空
        Note updateNote = new Note();
        updateNote.setCategoryId(null);
        noteMapper.update(updateNote, new LambdaQueryWrapper<Note>()
                .eq(Note::getCategoryId, id)
                .eq(Note::getUserId, userId)
        );

        // 删除分类
        categoryMapper.deleteById(id);
    }
}
