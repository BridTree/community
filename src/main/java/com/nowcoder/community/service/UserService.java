package com.nowcoder.community.service;


import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;
//域名和项目名的注入
    //域名
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        //对空值的处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMessagr","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMessagr","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMessagr","邮箱不能为空");
            return map;
        }

//        判断账号是否已存在

        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg","该账号已存在");
            return map;
        }

//        验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","该邮箱已存在");
            return map;
        }

//        注册用户，把用户信息存到库里
//        对密码的加密更改
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
//        用户状态
        user.setType(0);
        user.setStatus(0);
//        激活码获取
        user.setActivationCode(CommunityUtil.generateUUID());
//            随机头像设置
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
//注册时间
        user.setCreateTime(new Date());
//        把数据添加到库里
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        // http://localhost:8080/activation/101/code  （自定义激活路径
        //注册的时候，用户传进来的对象无id 但是，调用insertxxx语句后，就有了，（mybatis的配置有这么一项，自动配id
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);

        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);


        return map;
    }

    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;

        }else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String,Object> login(String username, String password,long expiredSeconds){
        Map<String,Object> map = new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空!");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","该账号不存在");
            return map;
        }

//        验证状态

        if(user.getStatus() == 0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }

//        验证密码 传入的是明文密码，要跟加密后的密码（库里存的）对比。把明文密码再用相同方式加密一下即可
        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码错误!");
            return map;
        }
//        登录成功后生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());

        return map;

    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    public LoginTicket findLoginTicket(String ticket){

        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId,String headerUrl){

        return userMapper.updateHeader(userId, headerUrl);
    }

    public User findUserByName(String username){

        return userMapper.selectByName(username);
    }


    public Map<String,Object> resetPassword(String email,String password){
        Map<String,Object> map = new HashMap<>();

        if(StringUtils.isBlank(email)){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        User user = userMapper.selectByEmail(email);
        if(user == null){
            map.put("emailMsg","该邮箱并未注册");
            return map;
        }

        password = CommunityUtil.md5(password + user.getSalt());
        userMapper.updatePassword(user.getId(), password);

        map.put("user",user);
        return map;
    }

}
