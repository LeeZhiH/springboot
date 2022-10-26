package com.zhihao.zhiyin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhihao.zhiyin.entity.Employee;
import com.zhihao.zhiyin.mapper.EmployeeMapper;
import com.zhihao.zhiyin.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author 86159
 * @time 2022/9/4 11:03
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
