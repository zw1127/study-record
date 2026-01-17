package cn.javastudy.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

// ArrayList的动态代理类
public class ArrayListProxy {

    public static void main(String[] args) {
        final List<String> list = new ArrayList< >();
        List<String> proxyInstance = (List<String>) Proxy.newProxyInstance(list.getClass().getClassLoader(),
            list.getClass().getInterfaces(),
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return method.invoke(list, args);
                }
            });

        proxyInstance.add("hello");
        System.out.println(list);
    }
}
