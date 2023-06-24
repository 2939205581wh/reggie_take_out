package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


/**
 *    员工管理 页面   控制层
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登入
     *
     * @param request  登入成功后将 employee 存入Session   通过request get一个Session
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
//        1.将页面提交的密码password进行md5加密处理
        String password = employee.getPassword(); //  获取密码
        password = DigestUtils.md5DigestAsHex(password.getBytes());  // DigestUtils 工具类

//        2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();  // 包装查询对象
        queryWrapper.eq(Employee::getUsername, employee.getUsername());  // 等值查询
//     因为  Username 字段为唯一属性  所以直接调用getOne
        Employee emp = employeeService.getOne(queryWrapper);

//        3.如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登入失败");
        }

//        4.密码比对，如果不一致则返回登录失败
        if (!emp.getPassword().equals(password)) {
            return R.error("登入失败");
        }

//        5.查看员工状态，如果为已禁用状态，则返回员工已禁用结果(0为禁用，1为可用)
        if (emp.getStatus() == 0) {
            return R.error("账号已被禁用");
        }

//        6.登录成功，将员工id存入Session并返回登陆成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }


    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 增加员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save( @RequestBody Employee employee) {  // @RequestBody 封装成JSON数据
        //  设置初始密码123456  进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //  设置  创建时间
//        employee.setCreateTime(LocalDateTime.now());
        //  设置 更新时间
//        employee.setUpdateTime(LocalDateTime.now());
        //  设置  当前用户登入的id  (创建人)
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        /**
         *    因为设置了通用类  MyMetaObjecthandler mybatisplus会自动填充其他字段
         */

        //  将对象 存入
        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * <p>
     * 不是JSON数据、 根据前端的参数
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
//        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);  //  一一对应
        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //  构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        /*
         *  添加过滤条件
         * */
//        (name--> 模糊查询)
//        StringUtils.isNotEmpty() 只有当name不为空值的情况 才会执行整个代码
//        StringUtils.isNotEmpty()  要导入org.apache.commons.lang这个包下的StringUtils
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

//        添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //  执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id 修改员工信息    即  编辑  启用/禁用
     *
     * @param employee 加上@RequestBody 封装成JSON数据
     * @return
     */
    /*
    *   问题 ：
* ==>  Preparing: UPDATE employee SET status=?, update_time=?, update_user=? WHERE id=?
==>Parameters: 0(Integer), 2023-05-23T20:56:47.978676700(LocalDateTime), 1(Long), 1660905731022499800(Long)
<==    Updates: 0
    * 为什么 id匹配不上
    *  因为  js对long型数据进行处理时 丢失了精度   会对最后几位数字进行4舍5入
    * 解决方法：
    *       在服务端给页面响应JSON数据时进行处理，将long型数据转化为String字符串
    * */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());

        /**
         * 查看是否 线程一致
         *
         * LoginCheckFilter
         * MyMetaObjecthandler.updateFill
         * EmployeeController.update
         */

        //   获取 线程id值
        Long id  =  Thread.currentThread().getId();
        //  查看是否 线程一致
        log.info("线程id为：{}",id);

        /**
         *    因为设置了通用类  MyMetaObjecthandler mybatisplus会自动填充
         */
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());   //  设置更新时间
        employee.setUpdateUser(empId);   //  设置当前登入用户
        employeeService.updateById(employee);

        return R.success("员工修改信息成功");
    }
//
    /*
     *   http://localhost:8080/backend/page/member/add.html?id= ····
     * 点击编辑后 会跳转到  新增页面
     * */

    /**   点击编辑    点击保存时 其实调用的是 新增员工 save方法
     * 根据 id 查询信息
     *  将点击要编辑的人员信息  全部都展示出 页面
     * @param id
     * @return
     */
    @GetMapping("/{id}")   // @PathVariable 路径变量 即id在请求路径里中
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询信息...");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);   //  将employee 封装成 JSON数据
        }
        return R.error("没有查询到对应的员工信息");
    }
}
