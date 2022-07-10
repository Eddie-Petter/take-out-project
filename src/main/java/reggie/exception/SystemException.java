package reggie.exception;

/**
 *自定义的系统异常
 */
public class SystemException extends RuntimeException{
    public SystemException(String message){
        super(message);
    }
}
