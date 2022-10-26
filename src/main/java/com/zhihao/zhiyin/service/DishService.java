package com.zhihao.zhiyin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhihao.zhiyin.dto.DishDto;
import com.zhihao.zhiyin.entity.Dish;


/**
 * @author 86159
 * @time 2022/9/18 16:12
 */
public interface DishService extends IService<Dish>  {

    /**
     * 新增菜品，同时插入菜品对应口味，同时操作两张表
     * @param dishDto
     */
    public void saveWithDishFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品和对应口味信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id);

    /**
     * 修改菜品及对应口味信息
     * @param dishDto
     */
    public void updateWithDishFlavor(DishDto dishDto);
}
