package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)//使用和main里一样的配置文件,现在以括号内的class为配置类了
//那个类想实现spring容器，就加入以下接口
class CommunityApplicationTests implements ApplicationContextAware {



	private  ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);

		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());

		alphaDao = applicationContext.getBean("alphaHibernate",AlphaDao.class); //得到的包转型为AlphaDao.class
		System.out.println(alphaDao.select());
	}

	@Test
	public  void testBeanManagement(){
		//容器获取service
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
		//bean只被实例化一次  要想多次实例化，看AlphaService文件的注释  不过一般都是单例
	}


	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat =
				applicationContext.getBean(SimpleDateFormat.class);

		System.out.println(simpleDateFormat.format(new Date()));
	}

	//以上一大串通过getBean的是主动获取，为Spring底层逻辑 而IOC思想的实现方式 依赖注入，并不需要这么麻烦
 //注入所用注解
	@Autowired
	//alphaDao 注入的不是默认的，可自定义注入
	@Qualifier("alphaHibernate") //括号里是bean的名字
	//spring容器把AlphaDao注入到alphaDao这个属性 就可以直接使用alphaDao这个属性
	private  AlphaDao alphaDao;

	@Test
	public void testDI(){
		System.out.println(alphaDao);
	}

}
