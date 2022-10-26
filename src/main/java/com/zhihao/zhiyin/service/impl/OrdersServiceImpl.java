package com.zhihao.zhiyin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.zhihao.zhiyin.common.BaseContext;
import com.zhihao.zhiyin.common.CustomException;
import com.zhihao.zhiyin.common.GlobalExceptionHandler;
import com.zhihao.zhiyin.dto.OrdersDto;
import com.zhihao.zhiyin.dto.SetmealDto;
import com.zhihao.zhiyin.entity.*;
import com.zhihao.zhiyin.mapper.OrdersMapper;
import com.zhihao.zhiyin.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 86159
 * @time 2022/10/24 15:20
 */
@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;
    /**
     * 用户下单
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获取当前用户ID
        Long userId = BaseContext.getCurrentId();
        //查询当前用户购物车
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

        if(shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空");
        }

        //查询用户信息
        User user = userService.getById(userId);

        //查询地址信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if(addressBook == null){
            throw new CustomException("用户地址信息有误");
        }

        long orderId = IdWorker.getId();//订单号


        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());



        //向订单表插入数据，一条
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);  //状态2为待派送
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(orders);

        //向订单明细表插入多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }

    /**
     * 分页查询订单,包含订单详情
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page pageWithDetail(int page, int pageSize) {

        Long userId = BaseContext.getCurrentId();

        Page<Orders> pageInfo = new Page(page, pageSize);
        Page<OrdersDto> dtoPageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,userId);
        queryWrapper.orderByDesc(Orders::getOrderTime);
        this.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,dtoPageInfo,"records");
        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> list =  records.stream().map((item) ->{
            OrdersDto ordersDto = new OrdersDto();

            BeanUtils.copyProperties(item,ordersDto);

            Long orderId = item.getId();
            LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(OrderDetail::getOrderId,orderId);
            List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper1);
            if (orderDetailList != null){ ;
                    ordersDto.setOrderDetails(orderDetailList);
            }
            return ordersDto;

        }).collect(Collectors.toList());



        dtoPageInfo.setRecords(list);
        return dtoPageInfo;
    }
}
