package reggie.common;


/**
 * 基于ThreadLocal的工具类，用来存放一些一个线程中公共的数据
 * 暂时只存放用户的id
 */

public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal();

    public static void setId(Long id){
        threadLocal.set(id);
    }

    public static Long getId(){
        return threadLocal.get();
    }
}
