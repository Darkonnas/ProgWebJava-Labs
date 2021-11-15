package demo2;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
@Aspect
public class PropertiesAspect {
    @Before("execution(* get*())")
    public void beforeGetter(JoinPoint joinPoint) throws NoSuchFieldException, IllegalAccessException {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String name = method.getName().substring("get".length()).toLowerCase(Locale.ROOT);
        Properties annotation = method.getDeclaringClass().getAnnotation(Properties.class);
        Field field = method.getDeclaringClass().getDeclaredField(name);
        Object target = joinPoint.getTarget();

        boolean fieldAccessibility = field.canAccess(target);

        if (!fieldAccessibility) {
            field.setAccessible(true);
        }

        if (field.get(target) == null) {
            field.set(target, ResourceBundle.getBundle(annotation.value()).getString(name));
        }

        if (!fieldAccessibility) {
            field.setAccessible(false);
        }
    }
}
