package com.zhihao.zhiyin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhihao.zhiyin.dto.SetmealDto;
import com.zhihao.zhiyin.entity.Setmeal;

import java.util.List;

/**
 * @author 86159
 * @time 2022/9/18 16:14
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 保存套餐及对应菜品关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐及对应菜品关联
     * @param ids
     */
    void removeWithDish(List<Long> ids);
}
