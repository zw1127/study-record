package cn.javastudy.springboot.aop.log.aspect;

import cn.javastudy.springboot.aop.log.annotation.Log;
import cn.javastudy.springboot.aop.log.domain.SysLog;
import cn.javastudy.springboot.aop.log.mapper.SysLogMapper;
import cn.javastudy.springboot.aop.log.util.HttpContextUtils;
import cn.javastudy.springboot.aop.log.util.IPUtils;
import java.lang.reflect.Method;
import java.util.Date;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

    private static final Logger LOG = LoggerFactory.getLogger(LogAspect.class);

    @Resource
    private SysLogMapper sysLogMapper;

    @Pointcut("@annotation(cn.javastudy.springboot.aop.log.annotation.Log)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public void around(ProceedingJoinPoint point) {
        long beginTime = System.currentTimeMillis();
        try {
            // 执行方法
            point.proceed();
        } catch (Throwable e) {
            LOG.error("proceed point error.", e);
        }
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        // 保存日志
        saveLog(point, time);
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLog sysLog = new SysLog();

        Log logAnnotation = method.getAnnotation(Log.class);
        if (logAnnotation != null) {
            // 注解上的描述
            sysLog.setOperation(logAnnotation.value());
        }
        // 请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");
        // 请求的方法参数值
        Object[] args = joinPoint.getArgs();
        // 请求的方法参数名称
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        if (args != null && paramNames != null) {
            String params = "";
            for (int i = 0; i < args.length; i++) {
                params += "  " + paramNames[i] + ": " + args[i];
            }
            sysLog.setParams(params);
        }
        // 获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        // 设置IP地址
        sysLog.setIp(IPUtils.getIpAddr(request));
        // 模拟一个用户名
        sysLog.setUserName("aopAspect");
        sysLog.setOperateTime((int) time);
        Date date = new Date();
        sysLog.setCreateTime(date);
        // 保存系统日志
        boolean result = sysLogMapper.insert(sysLog) > 0;
        LOG.info("insert data:{} into ds result:{}", sysLog, result);
    }

}
