package com.zhihao.zhiyin.dto;


import com.zhihao.zhiyin.entity.Setmeal;
import com.zhihao.zhiyin.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
