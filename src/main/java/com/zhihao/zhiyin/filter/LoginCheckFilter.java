package com.zhihao.zhiyin.filter;

import com.alibaba.fastjson.JSON;
import com.zhihao.zhiyin.common.BaseContext;
import com.zhihao.zhiyin.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * @author 86159
 * @time 2022/9/5 11:06
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    public static  final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    //路径匹配器，支持通配符

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取本次请求的URL
        String requestURI = request.getRequestURI();
        log.info("本次请求的路径：{}",requestURI);
        String[] urls = new String[]{
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/user/sendMsg",
            "/user/login",
            "/doc.html",
            "/webjars/**",
            "/swagger-resources",
            "/v2/api-docs"
        };
        //判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //如果不需要，直接放行
        if(check){
            log.info("本次请求无需处理{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //判断登录状态，如果已登录，直接放行
        if(request.getSession().getAttribute("employee") != null){

            //通过线程把用户id传到MyMetaObjectHandler中设置到数据库
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            log.info("用户已登录");
            filterChain.doFilter(request,response);
            return;
        }

        //判断登录状态，如果已登录，直接放行
        if(request.getSession().getAttribute("user") != null){

            //通过线程把用户id传到MyMetaObjectHandler中设置到数据库
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            log.info("用户已登录");
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        //如果为登录，放回未登录结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

//        log.info("拦截到请求：{}",request.getRequestURI());

    }

    public boolean check(String[] urls,String requestURI){

        for(String url : urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
