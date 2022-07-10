package reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggie.common.R;
import reggie.entity.Employee;
import reggie.entity.Orders;
import reggie.service.OrderService;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单：{}",orders.toString());
        orderService.submit(orders);
        return R.success("订单创建成功");
    }

    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize){
        log.info("page:{} pageSize:{}", page, pageSize);

        //添加分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //添加条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
//        wrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序
        wrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        orderService.page(pageInfo);
//        orderService.page(pageInfo,wrapper);
        //返回结果
        return R.success(pageInfo);
    }
}
