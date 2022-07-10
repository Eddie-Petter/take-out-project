package reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reggie.dto.SetmealDto;
import reggie.entity.Dish;
import reggie.entity.DishFlavor;
import reggie.entity.Setmeal;
import reggie.entity.SetmealDish;
import reggie.exception.BusinessException;
import reggie.mapper.SetmealMapper;
import reggie.service.SetmealDishService;
import reggie.service.SetmealService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        Long setmealId = setmealDto.getId();
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        list.stream().map((item) ->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(list);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishList  = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(setmealDishList);

        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        Long setmealId = setmealDto.getId();
        //删除已经关联的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        setmealDishService.remove(queryWrapper);

        //增加现在要关联的菜品
        List<SetmealDish> list = setmealDto.getSetmealDishes().stream().map((item) -> {
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(list);
    }

    @Override
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> setmealWrapper= new LambdaQueryWrapper<>();
        //统计还在售卖状态的菜品
        setmealWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        int count = this.count(setmealWrapper);

        if(count > 0){
            throw new BusinessException("套餐正在售卖，不能删除");
        }else {
            //先删除关联的口味表中的数据，然后再删除菜品数据
            LambdaQueryWrapper<SetmealDish> setmealDishWrapper = new LambdaQueryWrapper<>();
            setmealDishWrapper.in(SetmealDish::getSetmealId, ids);
            setmealDishService.remove(setmealDishWrapper);

            this.removeByIds(ids);
        }
    }

    @Override
    public void statusOff(Long ids) {
        Setmeal setmeal = this.getById(ids);
        setmeal.setStatus(0);
        this.updateById(setmeal);
    }

    @Override
    public void statusOn(Long ids) {
        Setmeal setmeal = this.getById(ids);
        setmeal.setStatus(1);
        this.updateById(setmeal);
    }
}
