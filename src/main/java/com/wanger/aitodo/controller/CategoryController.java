package com.wanger.aitodo.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wanger.aitodo.pojo.dto.CategoryDTO;
import com.wanger.aitodo.pojo.entity.Category;
import com.wanger.aitodo.pojo.result.Result;
import com.wanger.aitodo.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/categories")
@Tag(name="分类模块", description="处理Todo分类的增删改查")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Operation(summary = "添加新分类")
    @PostMapping("/add")
    public Result add(@RequestBody CategoryDTO categoryDTO) {
        categoryService.saveCategory(categoryDTO.getName());
        return Result.success();
    }

    @Operation(summary = "删除分类",description = "删除分类及其关联，当任务无关联时则删除任务")
    @DeleteMapping("/delete")
    public Result delete(@RequestBody CategoryDTO categoryDTO) {
        categoryService.delete(categoryDTO.getId());
        return Result.success();
    }

    @Operation(summary = "修改分类")
    @PutMapping("/update")
    public Result update(@RequestBody CategoryDTO categoryDTO) {

        categoryService.update(new LambdaUpdateWrapper<Category>()
                .eq(Category::getId, categoryDTO.getId())
                .set(Category::getName, categoryDTO.getName()));
        return Result.success();
    }

    @Operation(summary = "查寻分类列表")
    @GetMapping("/getAll")
    public Result getAll() {
        ArrayList<CategoryDTO> list = new ArrayList<CategoryDTO>();
        categoryService.list().forEach(category -> {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(category.getId());
            categoryDTO.setName(category.getName());
            list.add(categoryDTO);
        });
        return Result.success(list);
    }
}
