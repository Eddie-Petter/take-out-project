package reggie.exception;

/**
 * 自定义的业务异常
 */
public class BusinessException extends RuntimeException{
    public BusinessException(String message){
        super(message);
    }
}
