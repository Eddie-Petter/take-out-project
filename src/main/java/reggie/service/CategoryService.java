package reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggie.entity.Category;

public interface CategoryService extends IService<Category> {

    /**
     * 服务层新增方法的话要现在接口中声明，不能直接在实现类中写具体的内容
     */

    public void remove(Long ids);
}
