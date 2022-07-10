package reggie.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import reggie.common.MyMetaObjectHandler;

import javax.sql.DataSource;
import java.io.IOException;


/**
 * 卧槽你大爷啊！！！！！！
 * 终于知道为什么之前分不了页了啊！！
 * 为了实现公共字段自动注入，我没有选择和教程一样的setValue方法，而是用strictInsertFill方法
 * 因此得在MybatisSqlSessionFactoryBean中配置它
 * 但是因为有了MybatisSqlSessionFactoryBean，所以分页插件又不能自动配置了，
 * 需要我手动setPlugins一下
 */


@Configuration
//@MapperScan("reggie.mapper")

public class MybatisPlusConfig {

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

//    @Autowired
//    MybatisPlusInterceptor mybatisPlusInterceptor;
//
//    @Bean
//    public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws IOException {
//        MybatisSqlSessionFactoryBean mybatisPlus = new MybatisSqlSessionFactoryBean();
//        //加载数据源
//        mybatisPlus.setDataSource(dataSource);
//        //全局配置
//        GlobalConfig globalConfig  = new GlobalConfig();
//        //配置填充器
//        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());
//        mybatisPlus.setGlobalConfig(globalConfig);
//
//        //添加分页插件
//        Interceptor[] plugins = {mybatisPlusInterceptor};
//        mybatisPlus.setPlugins(plugins);
//
//
//        return mybatisPlus;
//    }


//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
//        return interceptor;
//    }
//
//    @Autowired
//    MybatisPlusInterceptor mybatisPlusInterceptor;
//
//    @Bean
//    public SqlSessionFactory sqlSessionFactory(DataSource druidDataSource)
//            throws Exception {
//        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
//        sessionFactory.setDataSource(druidDataSource);
//        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
//        sessionFactory.setMapperLocations(pathMatchingResourcePatternResolver.getResources("classpath*:mapper/*.xml"));
//        Interceptor[] plugins = {mybatisPlusInterceptor};//解决分页失效问题
//        sessionFactory.setPlugins(plugins);
//        return sessionFactory.getObject();
//    }





}
