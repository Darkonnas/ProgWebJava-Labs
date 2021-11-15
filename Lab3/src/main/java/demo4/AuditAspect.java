package demo4;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
public class AuditAspect {
    @After("execution(* *(..))")
    public void afterExecution(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        System.out.printf("Class %s, method %s, thread %s, user %s\n",
                method.getDeclaringClass().getName(),
                method.getName(),
                Thread.currentThread().getName(),
                System.getProperty("user.name"));
    }
}
