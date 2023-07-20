package cn.javastudy.springboot.aspect;

public aspect MyAspectJ {

    /**
     * 定义切点.
     */
    pointcut recordLog(): call(* cn.javastudy.springboot.aspect.UserService.greetUser(..));

    /**
     * 定义前置通知!
     */
    before(): authCheck(){
        System.out.println("sayHello方法执行前验证权限");
    }

    /**
     * 定义后置通知
     */
    after(): recordLog(){
        System.out.println("sayHello方法执行后记录日志");
    }

}