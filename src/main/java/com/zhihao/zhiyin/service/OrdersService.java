package com.zhihao.zhiyin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhihao.zhiyin.entity.Orders;

/**
 * @author 86159
 * @time 2022/10/24 15:15
 */
public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     */
    void submit(Orders orders);

    /**
     * 分页查询订单,包含订单详情
     * @param page
     * @param pageSize
     * @return
     */
    Page pageWithDetail(int page, int pageSize);
}
