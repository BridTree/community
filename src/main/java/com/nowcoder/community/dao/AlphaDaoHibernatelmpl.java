package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaHibernate") //给bean命名
public class AlphaDaoHibernatelmpl implements AlphaDao{
    @Override
    public  String select(){
        return  "Hibernate";
    }
}
