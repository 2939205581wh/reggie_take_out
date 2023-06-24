package com.itheima.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 *   检查用户是否已经完成登入
 *       过滤器
 * */
//  urlPatterns  拦截哪些请求路径
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //    路径匹配器   AntPathMatcher 工具类   支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;  //  向下转型  强转
        HttpServletResponse response = (HttpServletResponse) servletResponse;//  向下转型  强转
        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);   //  打印一些日志  进行分析

        // 定义不需要处理请求的路径   即 直接放行的路径
        //  只针对那些需要发送 controller请求的 url （即只要看不到该页面的数据即可）
        String[] urls = new String[]{
                "/employee/login",    //   点击登入的页面
                "/employee/logout",   //   退出的页面
                "/backend/**",   //  静态资源
                "/front/**",   //  静态资源
                "/common/**",   //  上传图片页面   不登入也可以正常访问该页面
        };


        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);


        //3.如果不需要处理，则直接放行   封装成一个方法（check）
        if (check) {
            log.info("本次请求{}不需求处理",requestURI);//  打印一些日志  进行分析
            filterChain.doFilter(request, response);
            return;
        }

        //4.判断登录状态，如果已经登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            //  打印一些日志  进行分析
            log.info("用户已登入，用户id为：{}",request.getSession().getAttribute("employee"));

            /**
             * 查看是否 线程一致
             *
             * LoginCheckFilter
             * MyMetaObjecthandler.updateFill
             * EmployeeController.update
             */

            //   获取 线程id值
            Long id  =  Thread.currentThread().getId();
            log.info("线程id为：{}",id);

            /**
             *   通过 HttpServletRequest request  获取 当前用户的 id
             */
            Long empId= (Long) request.getSession().getAttribute("employee");
            /**
             *  通过封装的工具类  将 id存入 该线程之中
             */
            BaseContext.setCurrentId(empId);


            filterChain.doFilter(request, response);
            return;
        }


        //5.如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        log.info("用户未登入");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /*
     *   路径匹配，检查本次请求是否需要放行
     * */
    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);// 匹配
            if (match) {
                return true;
            }
        }
        return false;
    }

}
