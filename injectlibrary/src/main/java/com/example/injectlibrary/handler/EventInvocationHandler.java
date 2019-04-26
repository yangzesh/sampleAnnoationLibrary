package com.example.injectlibrary.handler;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EventInvocationHandler implements InvocationHandler {
    //需要拦截的对象
    private Object target;
    private Map<String, Method> methodMap = new HashMap<>();

    public EventInvocationHandler(Object activity) {
        this.target = activity;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (target != null) {
            String methodName = method.getName();
            //将原有的方法替换为自定义方法
            method = methodMap.get(methodName);
            if (method != null) {
                //执行自定义的方法
                return method.invoke(target, args);
            }
        }
        return null;
    }

    /**
     * @param methodName 需要拦截的方法名
     * @param method     自定义的的方法
     */
    public void addMethod(String methodName, Method method) {
        methodMap.put(methodName, method);
    }

}
