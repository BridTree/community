package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prctotype")//此时，每次访问bean 会生成一个新的实例
public class AlphaService {

    //调用AlphaDao  与之后的查询方法一起看就是service依赖dao
    @Autowired
    private AlphaDao alphaDao;

    //构造器
    public  AlphaService(){
        System.out.println("实例化");
    }

    //增加初始化方法
    @PostConstruct //方法在构造器之后调用
    public void  init(){
        System.out.println("初始化");
    }

    //销毁
    @PreDestroy //在销毁对象前调用
    public  void  destroy(){
        System.out.println("销毁");
    }

    //模拟查询业务
    public  String find(){
        return alphaDao.select();
    }
}
