package com.zhihao.zhiyin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhihao.zhiyin.entity.AddressBook;
import com.zhihao.zhiyin.mapper.AddressBookMapper;
import com.zhihao.zhiyin.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author 86159
 * @time 2022/10/9 23:26
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
