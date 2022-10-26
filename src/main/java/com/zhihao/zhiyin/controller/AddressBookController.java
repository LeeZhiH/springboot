package com.zhihao.zhiyin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zhihao.zhiyin.common.BaseContext;
import com.zhihao.zhiyin.common.R;
import com.zhihao.zhiyin.entity.AddressBook;
import com.zhihao.zhiyin.entity.Employee;
import com.zhihao.zhiyin.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 86159
 * @time 2022/10/10 21:24
 */
@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    AddressBookService addressBookService;

    /**
     * 新增地址
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("新增地址成功");
    }


    /**
     * 地址显示
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 修改默认地址
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> isdefault(@RequestBody AddressBook addressBook){
        //先将该用户下的所有地址默认地址设置为0，既没有默认地址
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId()).set(AddressBook::getIsDefault,0);
        addressBookService.update(updateWrapper);

        //再单独设置默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 获取默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> defalut(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook one = addressBookService.getOne(queryWrapper);
        return R.success(one);
    }

    /**
     * 根据id查询地址，此次用户修改页面回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getById(@PathVariable Long id){
        AddressBook byId = addressBookService.getById(id);
        if(byId != null){
            return R.success(byId);
        }else {
            return R.error("没有查询到该地址信息");
        }
    }






    /**
     * 修改
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<AddressBook> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }
}
