package com.zhihao.zhiyin.service;

import com.zhihao.zhiyin.entity.MailRequest;

/**
 * @author 86159
 * @time 2022/10/9 22:38
 */
public interface SendMailService {

    /**
     * 简单文本邮件
     *
     * @param mailRequest
     * @return
     */
    void sendSimpleMail(MailRequest mailRequest);




}
