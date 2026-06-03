package com.notes.category;

import com.notes.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 分类接口
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取分类列表
     */
    @GetMapping
    public Result<?> list() {
        List<Map<String, Object>> data = categoryService.listCategories();
        return Result.ok(data);
    }

    /**
     * 创建分类
     */
    @PostMapping
    public Result<?> create(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String color = body.get("color");

        if (name == null || name.isBlank()) {
            return Result.error(400, "分类名称不能为空");
        }

        Map<String, Object> data = categoryService.createCategory(name, color);
        return Result.ok(data);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String name = body.get("name");
        String color = body.get("color");

        if (name == null || name.isBlank()) {
            return Result.error(400, "分类名称不能为空");
        }

        Map<String, Object> data = categoryService.updateCategory(id, name, color);
        return Result.ok(data);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.ok("删除成功");
    }
}
