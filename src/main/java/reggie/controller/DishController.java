package reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggie.common.R;
import reggie.dto.DishDto;
import reggie.entity.Category;
import reggie.entity.Dish;
import reggie.entity.DishFlavor;
import reggie.service.CategoryService;
import reggie.service.DishFlavorService;
import reggie.service.DishService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    DishService dishService;

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    CategoryService categoryService;


    /**
     * 新增菜品
     * @param dishDto 菜品信息和口味信息被包装在dishDto中
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("创建新菜品，dish:{}",dishDto.getId());
        dishService.saveWithFlavor(dishDto);
        return R.success("新菜品创建成功");
    }

    /**
     * 分页查询菜品信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        log.info("分页查询菜品信息");
        //创建分页构造器
        Page<Dish> dishPage = new Page<>(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,dishLambdaQueryWrapper);

        //因为Dish中包含的是分类的id而没有分类到名字，所以得用DishDto来作为返回值，其包含一个categoryName的属性
        //但是又因为dishService.page()（应该）只能处理Page<dish>类的对象，处理不了Page<DishDto>类的对象
        //所以只能先获取Page<dish>然后将其复制到Page<DishDto>中去
        Page<DishDto> dishDtoPage = new Page<>();
        //使用工具类BeanUtils的复制方法，要注意，Page的records属性是一个List属性，保存着查询得来的具体数据（那一堆菜的详细信息）
        //因此records属性（中的一堆Dish的信息）不应被复制到Page<DishDto>中
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        List<DishDto> dishDtoList = dishPage.getRecords().stream().map((item) ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());

            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询单个菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        log.info("根据id查询单个菜品，id:{}",id);
        return R.success(dishService.getByIdWithFlavor(id));
    }

    /**
     * 修改菜品数据
     * @param dishDto
     * @return
     */

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("修改菜品");
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 停售菜品
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> statusOff(@RequestParam List<Long> ids){
        log.info("批量停售菜品");
        for (Long id:ids) {
            dishService.statusOff(id);
        }
        return R.success("停售成功");
    }


    /**
     * 启售菜品
     * @param ids
     * @return
     */    @PostMapping("/status/1")
    public R<String> statusOn(@RequestParam List<Long> ids){
        log.info("批量启售菜品");
        for (Long id:ids) {
            dishService.statusOn(id);
        }
        return R.success("启售成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */

    /**
     * 这里代码搞错了，因为dish和dishFlavor表关联着，所以不能只删除dish而不删除对应的flavor
     * 记得要改
     * ok，改完了
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> removeList(@RequestParam List<Long> ids){
        log.info("删除套餐");
//        dishService.removeByIds(ids);
        dishService.removeWithFlavor(ids);
        return R.success("删除成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> getByCategoryId(Dish dish){
        log.info("根据categoryId和status查询");

        Long categoryId = dish.getCategoryId();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null,Dish::getCategoryId,categoryId);
        //已经停售的菜品就不要显示出来了嘛
        queryWrapper.eq(dish.getStatus() != null,Dish::getStatus,dish.getStatus());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishService.list(queryWrapper);

        //将dish封装到dishDto中，并补充categoryName和List<DishFlavor>信息
        List<DishDto> dishDtoList = dishes.stream().map((item) ->{
            //将Dish数据复制到DishDto中
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            //将categoryName存入DishDto
            String categoryName = categoryService.getById(categoryId).getName();
            dishDto.setCategoryName(categoryName);

            //将List<DishFlavor>存入DishDto
            LambdaQueryWrapper<DishFlavor> dishFlavorWrapper = new LambdaQueryWrapper<>();
            dishFlavorWrapper.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorWrapper);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }
}
