package reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggie.dto.DishDto;
import reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //对dishDto进行操作，保存dish的同时保存flavor
    public void saveWithFlavor(DishDto dishDto);
    public DishDto getByIdWithFlavor(Long dishID);
    public void updateWithFlavor(DishDto dishDto);
    public void removeWithFlavor(List<Long> ids);
    public void statusOff(Long ids);
    public void statusOn(Long ids);

}
