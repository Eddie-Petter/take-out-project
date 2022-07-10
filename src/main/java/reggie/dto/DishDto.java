package reggie.dto;

import reggie.entity.Dish;
import reggie.entity.DishFlavor;
import lombok.Data;
import reggie.entity.Dish;
import reggie.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
