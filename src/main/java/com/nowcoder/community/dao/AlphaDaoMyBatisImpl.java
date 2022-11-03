package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary//该注解加上，获得优先级
public class AlphaDaoMyBatisImpl implements  AlphaDao{
    @Override
    public String select(){
        return "MyBatis";
    }
}
