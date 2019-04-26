package com.example.injectlibrary.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//source 在源码阶段，只做检查操作，运行时会丢失，在编译时也会丢失
//CLASS  在编译时进行预操作，预处理，在class文件里面存在，运行时会丢失
//RUNTIME  运行时通过反射  jvm在运行时通过反射获取该注解的值

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ContentView {
    int value();
}
