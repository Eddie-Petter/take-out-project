package reggie;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement

public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功");

//        ApplicationContext ctx =  SpringApplication.run(ReggieApplication.class, args);
//
//        String[] beanNames = ctx.getBeanNamesForAnnotation(Configuration.class);
//
//        System.out.println("Service注解beanNames个数："+beanNames.length);
//
//        for(String bn:beanNames) {
//
//            System.out.println(bn);
//        }
//
//
//
//        ApplicationContext run = SpringApplication.run(ReggieApplication.class, args);
//
//        String[] beans = run.getBeanDefinitionNames();
//
//        for (String bean : beans) {
//
//            System.out.println(bean);
//
//        }
    }
}