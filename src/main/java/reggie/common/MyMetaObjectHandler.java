package reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;



/*
    每次客户端发送http请求，服务器端都会生成一个线程，在处理过程中，以下三个类的方法是在同一个线程之中的
    1、LoginCheckFilter的doFilter方法
    2、xxxController中的方法，例如update
    3、MyMetaObjectHandler中的方法，例如updateFill
    所以上面三个类的方法可以通过ThreadLocal来共享数据
    那么将用户id存入ThreadLocal的代码该写在哪呢？当前情况下写在Filter中用户已登录的分支是最好的
    一方面因为controller中有很多方法，每个方法都写一遍ThreadLocal很麻烦
    另一方面当前情况下Filter能够覆盖所有的用户已登录的情况（毕竟得登录了才能有id嘛）
 */

/**
 * 实现公共字段填充
 */




@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作，自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getId());
        metaObject.setValue("updateUser",BaseContext.getId());
    }

    /**
     * 更新操作，自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());

        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);

        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getId());
    }











//    @Override
//    public void insertFill(MetaObject metaObject) {
//        log.info("公共字段自动填充,insertFill...");
//        log.info(metaObject.toString());
//
////        metaObject.setValue("createTime", LocalDateTime.now());
////        metaObject.setValue("updateTime", LocalDateTime.now());
////        metaObject.setValue("createUser", BaseContext.getId());
////        metaObject.setValue("updateUser", BaseContext.getId());
//
//        this.strictInsertFill(metaObject,"updateTime",LocalDateTime.class,LocalDateTime.now());
//        this.strictInsertFill(metaObject,"createTime",LocalDateTime.class,LocalDateTime.now());
//        this.strictInsertFill(metaObject,"updateUser",Long.class,BaseContext.getId());
//        this.strictInsertFill(metaObject,"createUser",Long.class,BaseContext.getId());
//
//
//    }
//
//    @Override
//    public void updateFill(MetaObject metaObject) {
//        log.info("公共字段自动填充,updateFill...");
//        log.info(metaObject.toString());
//
////        metaObject.setValue("updateUser", BaseContext.getId());
////        metaObject.setValue("updateTime", LocalDateTime.now());
//
//        this.strictInsertFill(metaObject,"updateTime",LocalDateTime.class,LocalDateTime.now());
//        this.strictInsertFill(metaObject,"updateUser",Long.class,BaseContext.getId());
//    }
//
//    @Override
//    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
//        //if (metaObject.getValue(fieldName) == null) {
//            Object obj = fieldVal.get();
//            if (Objects.nonNull(obj)) {
//                metaObject.setValue(fieldName, obj);
//            }
//        //}
//        return this;
//    }
}

