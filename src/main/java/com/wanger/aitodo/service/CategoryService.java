package com.wanger.aitodo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanger.aitodo.pojo.entity.Category;

public interface CategoryService extends IService<Category> {

    void saveCategory(String name);

    void delete(Long id);
}
