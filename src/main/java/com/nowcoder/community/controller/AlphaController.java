package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha") //给这个类取一个访问名
public class AlphaController {

    //将service注入给controller
    @Autowired
    private AlphaService alphaService;

    //给方法提供一个访问路径
    @RequestMapping("/hello")
    //注解声明（使下面方法返回一个网页？
    @ResponseBody
    //给浏览器提供服务的方法
    public  String sayHello(){
        return "Hello Spring Boot.";
    }

    //模拟处理查询请求
    //依然要声明路径
    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    //SpringMVC下如何获得请求/响应对象（底层版）
    @RequestMapping("/http")
    //不用返回值的原因 通过response对象可以直接向浏览器输出任何数据 不依赖返回值
    //想获取请求对象，相应对象，在方法里声明就行了，声明类型后，dispatcherServlet在调方法后会自动把对象传给你
    public  void  http(HttpServletRequest request, HttpServletResponse response){
        //获取请求数据
        System.out.println(request.getMethod());//获取请求方式
        System.out.println(request.getServletPath());//获取请求路径
        Enumeration<String> enumeration = request.getHeaderNames();//得到所以请求行的key（请求行是key:value 结构
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();//key
            String value = request.getHeader(name);//value
            System.out.println(name + ":" + value);
        }
        //获取名为code的参数
        System.out.println(request.getParameter("code"));

        //返回相应数据
        //返回数据类型
        response.setContentType("text/html;charest=utf-8");//网页类型文本 字符集
        try(
                PrintWriter writer = response.getWriter();//获取输出流 直接放在try方法里，就可以不用再另写关闭数据流方法了
                ){

            writer.write("<h1>111</h1>");//输出一级标题 但是要输出全部页面还有一大堆，所以可见直接用底层的麻烦x
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //简单版
    //GET请求（默认）
    //  /students?current=1&limit=20   method确定请求方式
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    //那个getpatcheServlet把同参数的值赋上  如果是刚开始，无参数时，处理这种情况可以加注解
    //注解解释 name:参数名称  required=false:可以不传这个参数进来 defaultValue:默认值
    public  String getStudents(
            @RequestParam(name = "current",required = false,defaultValue = "1") int current,
            @RequestParam(name = "limit",required = false,defaultValue = "10")int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /student/123  （直接把参数加进路径）
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    //获取路径中的参数，通过以下注解
    public  String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    //POST请求 该方式传参时参数在路径，在明面上，而且路径有长度限制，即参数有个数限制
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    //获取参数  直接声明参数，参数与表单中参数一致即可
    public  String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应HTML数据（动态的HTML
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    //不加@RequestBody注解 默认返回HTML
    //dispatcherServlet把获取的Model View数据提交给模版引擎
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        //传动态的值 模版需要多少变量就弄多少
        mav.addObject("name","张三");
        mav.addObject("age",30);
        //模版路径和名字  模版放在templates下 只用写templates下级目录就行
        mav.setViewName("/demo/view"); //写文件名即可 因为其下文件是固定格式 view指view.html
        return mav;
    }

    //比上面略简单的响应方式
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    //不用ModelAndView 是简化的一个体现 返回的是view路径 Model数据通过参数得到
    //model对象是dispatcherS.. 运用方法时检测到Model，自动实例化对象
    public String getSchool(Model model){
        model.addAttribute("name", "北京大学");
        model.addAttribute("age",80);

        return "/demo/view"; //返回路径
    }

    //响应JSON数据  一般在异步请求中响应该数据 （当前网页不刷新，但是访问服务器了
    //返回JAVA对象，浏览器解析对象用的JS JSON可以实现两者兼容
    // JAVA对象 -> JSON字符串 -> JS对象

    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody //加该注解，向浏览器返回JSON
    //getpatcherServlet 把Map转为JSON字符串发送给浏览器
    public Map<String,Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",8000.00);
        return  emp;
    }

    //多个相似数据结构体 集合形式JSON字符串
    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody //加该注解，向浏览器返回JSON
    //getpatcherServlet 把Map转为JSON字符串发送给浏览器
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",23);
        emp.put("salary",8000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","李四");
        emp.put("age",24);
        emp.put("salary",9000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","王五");
        emp.put("age",25);
        emp.put("salary",10000.00);
        list.add(emp);

        return  list;
    }

    //cookie示例  返回的是字符串  通过response去响应。cookie存在了response头部，跟返回什么无关
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建cookie
        //给浏览器发送编码
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
//        设置生效范围
        cookie.setPath("/community/alpha");
        //给cookie设置生效时间，她会存在硬盘里，长期有效，直到时间过了。如果不设置，默认是存在内存里，一关就没了
        cookie.setMaxAge(60*10);
        //发送cookie
        response.addCookie(cookie);

        return "set cookie";

    }


    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
//    根据之前设置的参数code 直接取得cookie
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }


    //springmvc可以自动创建session
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session";
    }


    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    //ajax示例
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }

}
