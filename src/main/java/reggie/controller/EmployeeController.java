package reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import reggie.common.R;
import reggie.entity.Employee;
import reggie.service.EmployeeService;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 从前端获取用户名和密码进行登录，并将登录员工的id存入session中
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PostMapping(value = "/login")
    public R<Employee> login(HttpServletRequest httpServletRequest, @RequestBody Employee employee){
        //1、对输入的password做加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2查询数据库
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername,employee.getUsername());
        Employee sEmp = employeeService.getOne(wrapper);

        //3、验证用户名是否正确
        if(sEmp == null){
            return R.error("用户不存在");
        }

        //4、验证密码是否正确
        if(!sEmp.getPassword().equals(password)){
            return R.error("密码错误");
        }

        //5、验证用户是否被锁定
        if(sEmp.getStatus() == 0){
            return R.error("用户已被禁用");
        }

        //6、登录成功，将用户id存入session并返回登录成功结果
        httpServletRequest.getSession().setAttribute("employee",sEmp.getId());
        return R.success(sEmp);
    }

    /**
     * 将已登录的员工的id从session中删除
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest httpServletRequest){
        //清理session中保存的已登录员工的id
        httpServletRequest.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest httpServletRequest,@RequestBody Employee employee){
        log.info("新增员工，员工信息为：{}",employee.toString());

        //密码需要经过加密处理
        //给一个初始密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        employee.setCreateUser((Long) httpServletRequest.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long) httpServletRequest.getSession().getAttribute("employee"));

        employeeService.save(employee);

        return R.success("新增员工成功");
    }


    /**
     * 分页查询员工信息
     * @param page
     * @param pageSize
     * @param name   可带条件地查询员工信息（名字大致为xxx）
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize, String name){
        log.info("page:{} pageSize:{} name:{}", page, pageSize, name);

        //添加分页构造器
        Page pageInfo = new Page(page,pageSize);
        //添加条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        wrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序
        wrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,wrapper);
        //返回结果
        return R.success(pageInfo);
    }

    /**
     * 更新员工状态信息（禁用与启用）
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PutMapping
    public R<Employee> update(HttpServletRequest httpServletRequest, @RequestBody Employee employee){
        log.info("更新员工信息");

        //要更新的statue已经在传入的employee中了

        //记录更新时间与更新操作人
//        Long updateUser = (Long) httpServletRequest.getSession().getAttribute("employee");
//        employee.setUpdateUser(updateUser);
//        employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);

        return R.success(employee);
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }

}
