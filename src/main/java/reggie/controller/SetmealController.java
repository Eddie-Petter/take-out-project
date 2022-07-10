package reggie.controller;

import com.alibaba.druid.support.calcite.DDLColumn;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggie.common.BaseContext;
import reggie.common.R;
import reggie.dto.DishDto;
import reggie.dto.SetmealDto;
import reggie.entity.Dish;
import reggie.entity.Setmeal;
import reggie.entity.SetmealDish;
import reggie.entity.ShoppingCart;
import reggie.service.CategoryService;
import reggie.service.SetmealDishService;
import reggie.service.SetmealService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @Autowired
    CategoryService categoryService;

//    @Autowired
//    SetmealDishService setmealDishService;

    @PostMapping
    public R<String > save(@RequestBody SetmealDto setmealDto){
        log.info("新增套餐");
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("套餐分页查询");

        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        setmealService.page(setmealPage,queryWrapper);

        Page<SetmealDto> dtoPage = new Page<>();
        //dtoPage中的records存的应该是dto的list，而不是setmeal的list
        BeanUtils.copyProperties(setmealPage,dtoPage,"records");


        List<SetmealDto> setmealDtoList = setmealPage.getRecords()
                .stream().map((item) -> {
                    SetmealDto setmealDto = new SetmealDto();
                    BeanUtils.copyProperties(item,setmealDto);
                    Long categoryId = item.getCategoryId();
                    String categoryName = categoryService.getById(categoryId).getName();
                    setmealDto.setCategoryName(categoryName);

                    return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(setmealDtoList);

        return R.success(dtoPage);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        log.info("根据id查询套餐，id:{}",id);
        return R.success(setmealService.getByIdWithDish(id));
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("修改套餐");
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> removeList(@RequestParam List<Long> ids){
        log.info("删除套餐");
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }


    /**
     * 停售套餐
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> statusOff(Long[] ids){
        log.info("批量停售菜套餐");
        for (Long id:ids) {
            setmealService.statusOff(id);
        }
        return R.success("停售成功");
    }


    /**
     * 启售套餐
     * @param ids
     * @return
     */    @PostMapping("/status/1")
    public R<String> statusOn(Long[] ids){
        log.info("批量启售套餐");
        for (Long id:ids) {
            setmealService.statusOn(id);
        }
        return R.success("启售成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        log.info("根据categoryId和status查询");

        Long categoryId = setmeal.getCategoryId();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null, Setmeal::getCategoryId, categoryId);
        //已经停售的套餐就不要显示出来了嘛
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);

        return R.success(setmealList);
    }
}
