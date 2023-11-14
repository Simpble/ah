package com.shiep.aspect;

import com.shiep.annotation.NLog;
import com.shiep.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
/*本切面的作用为为添加了指定注解的方法记录日志*/
public class LogAspect {
    /*匹配com.shiep.controller包下的所有类所有方法*/
    @Pointcut("execution(* com.shiep.controller..*(..))")
    public void pc(){};



    @Before("pc()")
    public void recordLog(JoinPoint joinPoint){
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        Method method = methodSignature.getMethod();
        if(method == null){
//            log.info(signature.getName() + "获取到的方法为空");
            return;
        }
        NLog annotation = method.getAnnotation(NLog.class);
        if(annotation == null){
//            log.info(signature.getName() + "： 当前方法不存在该注解");
            return;
        }
        try {
            String logger =
                      "\n----------------------------------------------"
                    + "\n方法名: " + signature.getName()
                    + "\n参数集合： " + Arrays.deepToString(joinPoint.getArgs())
                    + "\n目标所在类： " + signature.getDeclaringTypeName()
                    + "\n----------------------------------------------"
                    ;
            FileUtil.recordLog(logger);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*至此完成初步模型，可以根据是否存在注解添加日志
        * 之后需要确定日志的模型，以及IO流的操作方式*/
    }
}
