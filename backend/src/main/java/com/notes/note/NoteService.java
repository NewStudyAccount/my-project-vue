package com.notes.note;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notes.auth.UserContext;
import com.notes.category.Category;
import com.notes.category.CategoryMapper;
import com.notes.common.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 分页查询笔记列表
     */
    public Map<String, Object> listNotes(String keyword, Long categoryId, int page, int pageSize) {
        Long userId = UserContext.getCurrentUserId();

        LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Note::getUserId, userId);
        if (categoryId != null) {
            wrapper.eq(Note::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            // title OR content 搜索，用 and() 包裹保证不破坏 userId 条件
            wrapper.and(w -> w
                    .like(Note::getTitle, keyword)
                    .or()
                    .like(Note::getContent, keyword)
            );
        }
        wrapper.orderByDesc(Note::getUpdatedAt);

        Page<Note> pageResult = noteMapper.selectPage(new Page<>(page, pageSize), wrapper);

        // 查询分类名称映射
        List<Note> notes = pageResult.getRecords();
        List<Long> categoryIds = notes.stream()
                .map(Note::getCategoryId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> categoryNameMap;
        if (!categoryIds.isEmpty()) {
            List<Category> categories = categoryMapper.selectBatchIds(categoryIds);
            categoryNameMap = categories.stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName));
        } else {
            categoryNameMap = new HashMap<>();
        }

        // 构造返回数据
        List<Map<String, Object>> list = notes.stream().map(note -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", note.getId().toString());
            item.put("title", note.getTitle());
            item.put("content", note.getContent());
            item.put("categoryId", note.getCategoryId() != null ? note.getCategoryId().toString() : null);
            item.put("categoryName", note.getCategoryId() != null ? categoryNameMap.getOrDefault(note.getCategoryId(), "未分类") : "未分类");
            item.put("createdAt", note.getCreatedAt());
            item.put("updatedAt", note.getUpdatedAt());
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", pageResult.getTotal());
        return data;
    }

    /**
     * 获取单条笔记
     */
    public Map<String, Object> getNote(Long id) {
        Long userId = UserContext.getCurrentUserId();
        Note note = noteMapper.selectById(id);
        if (note == null || !note.getUserId().equals(userId)) {
            throw new BusinessException("笔记不存在");
        }

        String categoryName = "未分类";
        if (note.getCategoryId() != null) {
            Category category = categoryMapper.selectById(note.getCategoryId());
            if (category != null) {
                categoryName = category.getName();
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", note.getId().toString());
        data.put("title", note.getTitle());
        data.put("content", note.getContent());
        data.put("categoryId", note.getCategoryId() != null ? note.getCategoryId().toString() : null);
        data.put("categoryName", categoryName);
        data.put("createdAt", note.getCreatedAt());
        data.put("updatedAt", note.getUpdatedAt());
        return data;
    }

    /**
     * 创建笔记
     */
    public Map<String, Object> createNote(String title, String content, Long categoryId) {
        Long userId = UserContext.getCurrentUserId();

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setCategoryId(categoryId);
        note.setUserId(userId);
        noteMapper.insert(note);

        return getNote(note.getId());
    }

    /**
     * 更新笔记
     */
    public Map<String, Object> updateNote(Long id, String title, String content, Long categoryId) {
        Long userId = UserContext.getCurrentUserId();
        Note note = noteMapper.selectById(id);
        if (note == null || !note.getUserId().equals(userId)) {
            throw new BusinessException("笔记不存在");
        }

        note.setTitle(title);
        note.setContent(content);
        note.setCategoryId(categoryId);
        noteMapper.updateById(note);

        return getNote(id);
    }

    /**
     * 删除笔记
     */
    public void deleteNote(Long id) {
        Long userId = UserContext.getCurrentUserId();
        Note note = noteMapper.selectById(id);
        if (note == null || !note.getUserId().equals(userId)) {
            throw new BusinessException("笔记不存在");
        }
        noteMapper.deleteById(id);
    }
}
