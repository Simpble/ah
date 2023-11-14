package com.shiep.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
/*设计该注解的目的：为项目的指定方法添加日志。当一个方法存在该注解时要求记录当前方法的日志*/
public @interface NLog {
    String value();
}
