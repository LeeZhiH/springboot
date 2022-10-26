package com.zhihao.zhiyin.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhihao.zhiyin.common.CustomException;
import com.zhihao.zhiyin.entity.Category;
import com.zhihao.zhiyin.entity.Dish;
import com.zhihao.zhiyin.entity.Setmeal;
import com.zhihao.zhiyin.mapper.CategoryMapper;
import com.zhihao.zhiyin.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 86159
 * @time 2022/9/18 10:13
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    @Autowired
    DishServiceImpl dishService;

    @Autowired
    SetmealServiceImpl setmealService;

    /**
     * 根据id删除分类，删除前需求进行业务处理，判断能否删除
     * @param id
     */
    @Override
    public void remove(Long id) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dishcount = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品
        if(dishcount > 0){
            //已经关联菜品，抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealcount = setmealService.count(setmealLambdaQueryWrapper);

        //查询当前分类是否关联了套餐
        if(setmealcount > 0){
            //已经关联套餐，抛出异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //正常删除
        super.removeById(id);
    }


}
