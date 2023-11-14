package com.shiep.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/*设计该注解的目的为进行身份验证,与JWT功能重合*/
@Target(ElementType.METHOD)
@Inherited
public @interface Position {
}
