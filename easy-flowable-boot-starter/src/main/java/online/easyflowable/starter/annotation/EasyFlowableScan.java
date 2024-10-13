package online.easyflowable.starter.annotation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: MoJie
 * @Date: 2024-10-06 13:24
 * @Description: 自定义扫描注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@MapperScan({"online.easyflowable.core"})
@ComponentScan(basePackages = {"online.easyflowable"})
public @interface EasyFlowableScan {

}
