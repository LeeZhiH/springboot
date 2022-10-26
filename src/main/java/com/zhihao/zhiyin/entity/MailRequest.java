package com.zhihao.zhiyin.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 86159
 * @time 2022/10/9 22:36
 * 邮件类
 */
@Data
public class MailRequest implements Serializable {
    /**
     * 接收人
     */
    private String sendTo;

    /**
     *  邮件主题
     */
    private String subject;

    /**
     *  邮件内容
     */
    private String text;

    /**
     *  附件路径
     */
    private String filePath;



}
