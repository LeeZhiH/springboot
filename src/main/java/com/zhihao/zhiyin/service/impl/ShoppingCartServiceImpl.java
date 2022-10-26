package com.zhihao.zhiyin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhihao.zhiyin.entity.ShoppingCart;
import com.zhihao.zhiyin.mapper.ShoppingCartMapper;
import com.zhihao.zhiyin.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author 86159
 * @time 2022/10/17 20:47
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
