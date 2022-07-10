package reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggie.common.BaseContext;
import reggie.common.R;
import reggie.entity.OrderDetail;
import reggie.entity.ShoppingCart;
import reggie.exception.BusinessException;
import reggie.service.ShoppingCartService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 往购物车中添加菜品/套餐
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("添加购物车数据");
        Long userId = BaseContext.getId();
        shoppingCart.setUserId(userId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null,ShoppingCart::getUserId,userId);

        //判断是菜品还是套餐
        if(shoppingCart.getDishId() != null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart queryShoppingCart = shoppingCartService.getOne(queryWrapper);
        //如果菜品/套餐已存在，则直接将number加一
        if(queryShoppingCart != null){
            int number = queryShoppingCart.getNumber();
            queryShoppingCart.setNumber(number + 1);
            shoppingCartService.updateById(queryShoppingCart);
        }else {
            //如果菜品/套餐未存在，则将其添加至数据库
            queryShoppingCart = shoppingCart;
            queryShoppingCart.setNumber(1);
            queryShoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(queryShoppingCart);
        }

        return R.success(queryShoppingCart);
    }

    /**
     *减少购物车中的菜品/套餐
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("添加购物车数据");
        Long userId = BaseContext.getId();
        shoppingCart.setUserId(userId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null,ShoppingCart::getUserId,userId);

        if(shoppingCart.getDishId() != null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart queryShoppingCart = shoppingCartService.getOne(queryWrapper);

        if(queryShoppingCart != null){
            int number = queryShoppingCart.getNumber();
            if(number > 1){
                queryShoppingCart.setNumber(number - 1);
                shoppingCartService.updateById(queryShoppingCart);
            }else {
                //如果数量不到一个那就直接删除了
                shoppingCartService.removeById(queryShoppingCart.getId());
            }

        }else {
            throw new BusinessException("商品不存在");
        }

        return R.success(queryShoppingCart);

    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart>  list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getId());
        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }
}
