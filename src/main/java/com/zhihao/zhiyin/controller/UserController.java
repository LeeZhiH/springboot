package com.zhihao.zhiyin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhihao.zhiyin.common.R;
import com.zhihao.zhiyin.entity.MailRequest;
import com.zhihao.zhiyin.entity.User;
import com.zhihao.zhiyin.service.SendMailService;
import com.zhihao.zhiyin.service.UserService;
import com.zhihao.zhiyin.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author 86159
 * @time 2022/10/8 11:08
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    SendMailService sendMailService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession){
        //获取手机号
        String phone = user.getPhone();

        //生成随机验证码
        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("code={}",code);


        //发送邮件
//            MailRequest mailRequest = new MailRequest();
//            mailRequest.setSendTo("2940402378@qq.com");
//            mailRequest.setSubject("只因外卖");
//            mailRequest.setText("验证码为" + code);
//            sendMailService.sendSimpleMail(mailRequest);

            //将生成验证码保存到sessions
            httpSession.setAttribute(phone,code);
            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    /**
     * 登录
     * @param map
     * @param httpSession
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession httpSession){
        //获取手机号和验证码
        String phone = map.get("phone").toString();

        String code = map.get("code").toString();

        Object codeInSession = httpSession.getAttribute(phone);
        //验证码比对
        if(codeInSession != null && codeInSession.equals(code)){


            //判断当前用户是否注册，如未注册则自动注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);

            if(user == null){
                user  = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            httpSession.setAttribute("user",user.getId());
            return R.success(user);

        }
        return R.error("验证码错误");
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出登录");
    }


}
