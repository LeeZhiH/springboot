package com.zhihao.zhiyin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhihao.zhiyin.common.CustomException;
import com.zhihao.zhiyin.dto.SetmealDto;
import com.zhihao.zhiyin.entity.DishFlavor;
import com.zhihao.zhiyin.entity.Setmeal;
import com.zhihao.zhiyin.entity.SetmealDish;
import com.zhihao.zhiyin.mapper.SetmealMapper;
import com.zhihao.zhiyin.service.SetmealDishService;
import com.zhihao.zhiyin.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 86159
 * @time 2022/9/18 16:15
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 保存套餐及对应菜品关系
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        Long setmealDtoId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) ->{
            item.setSetmealId(setmealDtoId);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐及对应菜品关联
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {

        //查询对应套餐状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);

        if(count > 0){
            //如果状态为售卖中，抛出异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除setmeal表中套餐数据
        this.removeByIds(ids);
        //删除setmealdish表中套餐菜品关系
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(queryWrapper1);

    }
}
