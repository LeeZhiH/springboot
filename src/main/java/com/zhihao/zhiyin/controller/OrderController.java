package com.zhihao.zhiyin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhihao.zhiyin.common.BaseContext;
import com.zhihao.zhiyin.common.R;
import com.zhihao.zhiyin.entity.Employee;
import com.zhihao.zhiyin.entity.Orders;
import com.zhihao.zhiyin.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 86159
 * @time 2022/10/24 15:23
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){

        ordersService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> page(int page,int pageSize){
        log.info("page = {},pageSize = {}",page,pageSize);


        //带订单详情的分页查询
        Page pageInfo = ordersService.pageWithDetail(page, pageSize);

        return R.success(pageInfo);


    }
}
