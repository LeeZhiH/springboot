package com.zhihao.zhiyin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhihao.zhiyin.common.BaseContext;
import com.zhihao.zhiyin.common.R;
import com.zhihao.zhiyin.entity.ShoppingCart;
import com.zhihao.zhiyin.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 86159
 * @time 2022/10/12 16:49
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 购物车列表
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 新增到购物车
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车{{}",shoppingCart);
        //设置用户id，指定哪个用户的购物车
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //查询当前菜品是否在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());

        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        queryWrapper.eq(dishId != null,ShoppingCart::getDishId,dishId);
        queryWrapper.eq(setmealId != null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());


        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        //如果已经存在，在原来的基础加一
        if(one != null){
            Integer number = one.getNumber();
            one.setNumber( number + 1);
            shoppingCartService.updateById(one);
        }else {
            //如果不存在，添加数量一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);
    }

    /**
     * 减少购物车数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车{{}",shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //查询当前菜品是否在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        queryWrapper.eq(dishId != null,ShoppingCart::getDishId,dishId);
        queryWrapper.eq(setmealId != null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());


        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        log.info("one{}",one);
        if(one.getNumber() == 1){
            //如果数量为1，删除该列
            shoppingCartService.removeById(one);
            one.setNumber(0);
//            one = shoppingCart;

        }else {
            //如果不为1，在原来的基础减一
            Integer number = one.getNumber();
            one.setNumber( number - 1);
            shoppingCartService.updateById(one);
        }
        return R.success(one);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long userid = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userid);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}
