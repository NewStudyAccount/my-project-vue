package com.notes.note;

import com.notes.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 笔记接口
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    /**
     * 获取笔记列表（分页 + 搜索 + 分类筛选）
     */
    @GetMapping
    public Result<?> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> data = noteService.listNotes(keyword, categoryId, page, pageSize);
        return Result.ok(data);
    }

    /**
     * 获取单条笔记
     */
    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        Map<String, Object> data = noteService.getNote(id);
        return Result.ok(data);
    }

    /**
     * 创建笔记
     */
    @PostMapping
    public Result<?> create(@RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        Long categoryId = body.get("categoryId") != null
                ? Long.parseLong(body.get("categoryId").toString())
                : null;

        Map<String, Object> data = noteService.createNote(title, content, categoryId);
        return Result.ok(data);
    }

    /**
     * 更新笔记
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        Long categoryId = body.get("categoryId") != null
                ? Long.parseLong(body.get("categoryId").toString())
                : null;

        Map<String, Object> data = noteService.updateNote(id, title, content, categoryId);
        return Result.ok(data);
    }

    /**
     * 删除笔记
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        noteService.deleteNote(id);
        return Result.ok("删除成功");
    }
}
