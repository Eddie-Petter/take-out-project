package reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reggie.exception.BusinessException;
import reggie.exception.SystemException;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public R<String> BusinessExceptionHandler(BusinessException ex){
        log.info("业务异常"+ ex.toString());
        return R.error(ex.getMessage());
    }

    @ExceptionHandler(SystemException.class)
    public R<String> SystemExceptionHandler(SystemException ex){
        log.info("系统异常"+ ex.toString());
        return R.error(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R<String> exceptionHandler(Exception ex){
        log.error("其他异常"+ex.toString());
        if(ex.getMessage().contains("Duplicate entry")){
//            String[]msg = ex.getMessage().split(" ");
//            return R.error(msg[2] + "已存在");
            return R.error("对象已存在");
        }
        return R.error("未知错误");
    }

}
