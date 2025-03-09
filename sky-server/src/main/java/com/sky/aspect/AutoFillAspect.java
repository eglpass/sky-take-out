package com.sky.aspect;


import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import javassist.bytecode.SignatureAttribute;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType value = autoFill.value();
        Object[] args = joinPoint.getArgs();
        if(args == null||args.length==0){
            return;
        }
        Object entity = args[0];
        LocalDateTime now = LocalDateTime.now();
        Long baseContext = BaseContext.getCurrentId();
        switch (value){
            case INSERT:
                try {
                    Method createTimeMethod = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                    createTimeMethod.invoke(entity,now);
                    Method createUserMethod = entity.getClass().getDeclaredMethod("setCreateUser", Long.class);
                    createUserMethod.invoke(entity,baseContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
                case UPDATE:
                    try {
                        Method updateUserMethod = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                        updateUserMethod.invoke(entity,baseContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
        }

    }

}
