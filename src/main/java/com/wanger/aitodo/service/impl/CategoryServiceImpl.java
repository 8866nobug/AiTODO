package com.wanger.aitodo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanger.aitodo.mapper.CategoryMapper;
import com.wanger.aitodo.mapper.TaskCategoryRelMapper;
import com.wanger.aitodo.pojo.entity.Category;
import com.wanger.aitodo.pojo.entity.TaskCategoryRel;
import com.wanger.aitodo.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    private TaskCategoryRelMapper taskCategoryRelMapper;

    @Override
    public void saveCategory(String name) {
        Category category = new Category();
        category.setName(name);
        this.save(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        removeById(id);
        taskCategoryRelMapper.delete(new LambdaQueryWrapper<TaskCategoryRel>().eq(TaskCategoryRel::getCategoryId, id));

    }
}
