package com.example.injectlibrary;

import android.app.Activity;
import android.view.View;

import com.example.injectlibrary.annotation.BaseEvent;
import com.example.injectlibrary.annotation.ContentView;
import com.example.injectlibrary.annotation.InjectView;
import com.example.injectlibrary.handler.EventInvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InjectManager {

    public static void inject(Activity activity) {
        //布局注入
        injectLayout(activity);
        //控件注入
        injectViews(activity);
        //事件注入
        injectEvents(activity);

    }

    private static void injectEvents(Activity activity) {
        Class<? extends Activity> activityClass = activity.getClass();
        //获取本类所有的方法
        Method[] declaredMethods = activityClass.getDeclaredMethods();
        //遍历方法
        for (Method method : declaredMethods) {
            //获取方法上的所有属性
            Annotation[] annotations = method.getAnnotations();
            //遍历属性
            for (Annotation annotation : annotations) {
                //获取Onclick注解上的注解类型
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (annotationType != null) {
                    //根据注解类型获取注解的值
                    BaseEvent baseEvent = annotationType.getAnnotation(BaseEvent.class);
                    //事件三要素
                    String lintenerSet = baseEvent.listenerSet();
                    Class<?> listenerType = baseEvent.listenerType();
                    String callBack = baseEvent.callBack();
                    try {
                        Method value = annotationType.getDeclaredMethod("value");
                        int[] views = (int[]) value.invoke(annotation);
                        //拦截方法实现自定义方法
                        EventInvocationHandler eventInvocationHandler = new EventInvocationHandler(activity);
                        //把自定义的方法添加进去
                        eventInvocationHandler.addMethod(callBack, method);
                        //创建动态代理
                        Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, eventInvocationHandler);
                        for (int viewId : views) {
                            //获取当前的view
                            View viewById = activity.findViewById(viewId);
                            Method setMethod = viewById.getClass().getMethod(lintenerSet, listenerType);
                            setMethod.invoke(viewById, listener);
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private static void injectViews(Activity activity) {
        Class<? extends Activity> activityClass = activity.getClass();
        Field[] declaredFields = activityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            InjectView annotation = field.getAnnotation(InjectView.class);
            if (annotation != null) {
                //获取注解的值
                int value = annotation.value();
                try {
                    Method findViewById = activityClass.getMethod("findViewById", int.class);
                    //执行findViewById方法，获取View对象
                    View view = (View) findViewById.invoke(activity, value);
                    field.setAccessible(true);//允许通过反射来给私有属性设置
                    //属性的值赋值给当控件，当前的Activity
                    field.set(activity, view);//将属性的值赋值给控件,注意当属性为private，不能赋值
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void injectLayout(Activity activity) {
        Class<? extends Activity> activityClass = activity.getClass();
        ContentView annotation = activityClass.getAnnotation(ContentView.class);
        int layoutId = annotation.value();
        try {
            Method setContentView = activityClass.getMethod("setContentView", int.class);
            setContentView.invoke(activity, layoutId);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
