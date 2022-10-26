package com.zhihao.zhiyin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhihao.zhiyin.entity.OrderDetail;
import com.zhihao.zhiyin.mapper.OrderDetailMapper;
import com.zhihao.zhiyin.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author 86159
 * @time 2022/10/24 15:22
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
