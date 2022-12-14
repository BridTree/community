package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //写所用到的方法，之后再配置类里写对应的sql语句
//    该方法对应的是一个动态sql  offset:每一页起始行行号 limit:每页限制行数
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);
    //param注解，是给参数取一个别名  以及 如果需要动态的拼一个条件，并且该方法只有一个条件，这时候就要有一个param
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);
}
