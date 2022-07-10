package reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reggie.dto.DishDto;
import reggie.entity.Dish;
import reggie.entity.DishFlavor;
import reggie.exception.BusinessException;
import reggie.mapper.DishMapper;
import reggie.service.DishFlavorService;
import reggie.service.DishService;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {
    @Autowired
    DishFlavorService dishFlavorService;

    @Override
    //因为涉及到两个表的操作，所以需要开启事务
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //因为dishDto继承至dish，所以可以直接用dishService.save()来保存
        this.save(dishDto);

        Long dishId = dishDto.getId();
        for (DishFlavor dishFlavor:dishDto.getFlavors()){
            dishFlavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(dishDto.getFlavors());

    }

    @Override
    public DishDto getByIdWithFlavor(Long dishID) {
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(dishID);

        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> flavorWrapper = new LambdaQueryWrapper<>();
        flavorWrapper.eq(DishFlavor::getDishId,dishID);
        List<DishFlavor> list = dishFlavorService.list(flavorWrapper);

        dishDto.setFlavors(list);

        return  dishDto;
    }

    /**
     * update和save还是有区别的，因为update要先删除已有的，然后再加上新增的
     * @param dishDto
     */


    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        //先清理当前菜品对应口味数据---dish_flavor表的delete操作
        Long dishId = dishDto.getId();

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishId);
        dishFlavorService.remove(queryWrapper);

        //再添加当前提交过来的口味数据---dish_flavor表的insert操作
        for (DishFlavor dishFlavor:dishDto.getFlavors()){
            dishFlavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(dishDto.getFlavors());

    }

    @Override
    public void removeWithFlavor(List<Long> ids) {
        LambdaQueryWrapper<Dish> dishWrapper= new LambdaQueryWrapper<>();
        //统计还在售卖状态的菜品
        dishWrapper.in(Dish::getId,ids).eq(Dish::getStatus,1);
        int count = this.count(dishWrapper);

        if(count > 0){
            throw new BusinessException("菜品正在售卖，不能删除");
        }else {
            //先删除关联的口味表中的数据，然后再删除菜品数据
            LambdaQueryWrapper<DishFlavor> flavorWrapper = new LambdaQueryWrapper<>();
            flavorWrapper.in(DishFlavor::getDishId, ids);
            dishFlavorService.remove(flavorWrapper);

            this.removeByIds(ids);
        }
    }

    @Override
    public void statusOff(Long ids) {
        Dish dish = this.getById(ids);
        dish.setStatus(0);
        this.updateById(dish);
    }

    @Override
    public void statusOn(Long ids) {
        Dish dish = this.getById(ids);
        dish.setStatus(1);
        this.updateById(dish);
    }
}
