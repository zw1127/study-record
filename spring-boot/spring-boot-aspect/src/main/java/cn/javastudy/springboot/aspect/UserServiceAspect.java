package cn.javastudy.springboot.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserServiceAspect {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceAspect.class);

    @Around("execution(* cn.javastudy.springboot.aspect.UserService.greetUser(String)) && args(username)")
    public Object aroundProcessUser(ProceedingJoinPoint joinPoint, String username) throws Throwable {
        LOG.info("Before method: {}", username);
        // 调用目标方法
        Object result = joinPoint.proceed();

        return result;
    }

}
