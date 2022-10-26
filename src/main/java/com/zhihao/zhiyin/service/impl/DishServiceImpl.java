package com.zhihao.zhiyin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhihao.zhiyin.dto.DishDto;
import com.zhihao.zhiyin.entity.Dish;
import com.zhihao.zhiyin.entity.DishFlavor;
import com.zhihao.zhiyin.mapper.DishMapper;
import com.zhihao.zhiyin.service.DishFlavorService;
import com.zhihao.zhiyin.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 86159
 * @time 2022/9/18 16:14
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应口味
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithDishFlavor(DishDto dishDto) {
        //保存菜品信息到菜品表Dish
        this.save(dishDto);

        Long dishId = dishDto.getId();


        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) ->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味信息到DishFlavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     *根据id查询菜品和对应口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询对应口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavor = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavor);


        return dishDto;
    }

    /**
     * 修改菜品及对应口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithDishFlavor(DishDto dishDto) {
        //修改dish表的字段
        this.updateById(dishDto);

        //修改dishflavor表的字段
        //1.清理当前菜品对应的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);


        //2.添加当前提交的菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item) ->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味信息到DishFlavor
        dishFlavorService.saveBatch(flavors);
    }
}
