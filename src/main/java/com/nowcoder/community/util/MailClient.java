package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static sun.management.Agent.error;

@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);
    //用的以下核心组件也是由spring容器管理，直接注入
    @Autowired
    private JavaMailSender mailSender;
    //固定的发件人 所用直接注入
    @Value("${spring.mail.username}")
    private String from;

    //封装公有方法被外界调用
    public void sendMail(String to,String subject, String content){

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
//            如果不加参数，默认文本是普通文本，是文本。如果加了参数，允许支持html文本
            helper.setText(content,true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败" + e.getMessage());
        }
    }

}
