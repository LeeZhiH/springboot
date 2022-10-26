package com.zhihao.zhiyin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhihao.zhiyin.entity.User;
import com.zhihao.zhiyin.mapper.UserMapper;
import com.zhihao.zhiyin.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author 86159
 * @time 2022/10/8 11:07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
