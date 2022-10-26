package com.zhihao.zhiyin.dto;


import com.zhihao.zhiyin.entity.Dish;
import com.zhihao.zhiyin.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;


@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
