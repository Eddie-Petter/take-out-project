package reggie.dto;

import reggie.entity.Setmeal;
import reggie.entity.SetmealDish;
import lombok.Data;
import reggie.entity.Setmeal;
import reggie.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
