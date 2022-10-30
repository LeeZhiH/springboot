package com.zhihao.zhiyin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhihao.zhiyin.common.R;
import com.zhihao.zhiyin.dto.DishDto;
import com.zhihao.zhiyin.entity.Category;
import com.zhihao.zhiyin.entity.Dish;
import com.zhihao.zhiyin.entity.DishFlavor;
import com.zhihao.zhiyin.entity.Employee;
import com.zhihao.zhiyin.service.CategoryService;
import com.zhihao.zhiyin.service.DishFlavorService;
import com.zhihao.zhiyin.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 86159
 * @time 2022/9/22 21:58
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){

        log.info(dishDto.toString());

        dishService.saveWithDishFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 分页查询菜品信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        //执行查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) ->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();

            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){

        log.info("修改菜品");
        dishService.updateWithDishFlavor(dishDto);

        //清理某个分类下的redis缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }

    /**
     * 根据id查找菜品及对应口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 根据条件查询对应菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        List<DishDto> dtoList = null;

        //动态构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //先从redis中获取缓存数据
        dtoList  = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if(dtoList != null){
            //如果存在缓存，直接返回数据
            return R.success(dtoList);
        }

        //如果不存在，查询数据库
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.like(dish.getName() != null,Dish::getName,dish.getName());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        dtoList = list.stream().map((item) ->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(flavorList);

            Long categoryId = item.getCategoryId();

            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        //将查询到的数据缓存到redis中
        redisTemplate.opsForValue().set(key,dtoList,60, TimeUnit.MINUTES);

        return R.success(dtoList);
    }


    /**
     * 修改菜品状态，起售停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}{ids}")
    public R<String> status(@PathVariable Integer status,@RequestParam Long ids){
        log.info("status：{}",status);
        log.info("id:{}",ids);
        if(status == 0){
            LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Dish::getId,ids).set(Dish::getStatus,0);
            dishService.update(updateWrapper);
        }else{
            LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Dish::getId,ids).set(Dish::getStatus,1);
            dishService.update(updateWrapper);
        }
        return R.success("修改状态成功");
    }
}
