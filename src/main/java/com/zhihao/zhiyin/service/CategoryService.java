package com.zhihao.zhiyin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhihao.zhiyin.entity.Category;

/**
 * @author 86159
 * @time 2022/9/18 10:12
 */
public interface CategoryService extends IService<Category> {

    void remove(Long id);
}
