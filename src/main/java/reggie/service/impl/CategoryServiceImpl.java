package reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reggie.entity.Category;
import reggie.entity.Dish;
import reggie.entity.Setmeal;
import reggie.exception.BusinessException;
import reggie.mapper.CategoryMapper;
import reggie.mapper.DishMapper;
import reggie.service.CategoryService;
import reggie.service.DishService;
import reggie.service.SetmealService;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    DishService dishService;

    @Autowired
    SetmealService setmealService;

    /**
     * 如果没有菜品或套餐与该分类关联，则删除之，否则抛出业务异常
     * @param ids
     */
    @Override
    public void remove(Long ids) {
        //有菜品关联该类，不能删除，返回业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int countDish = dishService.count(dishLambdaQueryWrapper);
        if(countDish > 0){
             throw new BusinessException("当前分类关联了菜品，不能删除");
        }
        //有套餐关联该类，不能删除，返回业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int countSetmeal = setmealService.count(setmealLambdaQueryWrapper);
        if(countSetmeal > 0){
            throw new BusinessException("当前分类关联了套餐，不能删除");
        }
        //无菜品或套餐关联该类，正常删除
        this.removeById(ids);
    }
}
