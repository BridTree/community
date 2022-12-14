package com.nowcoder.community;


import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;
//thymeleaf也可以通过spring实现自动注入
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("1226712489@qq.com","TEST","Welcom");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","sun");

        String content= templateEngine.process("/mail/demo",context);
        System.out.println(content);

        mailClient.sendMail("1226712489@qq.com","HTML",content);
    }
}
