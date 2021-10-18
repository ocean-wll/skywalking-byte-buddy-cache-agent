package com.shulie.agent;

import com.shulie.agent.config.AgentConfig;
import com.shulie.agent.interceptor.CacheInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

/**
 * @Description agent入口类
 * @Author ocean_wll
 * @Date 2021/8/4 1:53 下午
 */
public class Agent {

    public static void premain(final String agentArgs, final Instrumentation instrumentation) throws Exception {
        System.err.println("====== skywalking-byte-buddy-agent ======");
        // 预处理启动参数
        AgentConfig.instance().initConfig();

        if (AgentConfig.enable) {
            System.err.println("=== begin start skywalking-byte-buddy-agent ===");
            System.out.println("=== cacheMode is " + AgentConfig.cacheMode + " ===");
            AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) -> builder
                    // 拦截transform方法
//                    .method(ElementMatchers.hasMethodName("transform"))
                    .method(ElementMatchers.hasMethodName("transform")
                                    .and(ElementMatchers.takesArguments(5))
//                            .and(ElementMatchers.takesArgument(0,ClassLoader.class))
//                            .and(ElementMatchers.takesArgument(1,String.class))
//                            .and(ElementMatchers.takesArgument(2,Class.class))
//                            .and(ElementMatchers.takesArgument(3,ProtectionDomain.class))
//                            .and(ElementMatchers.takesArgument(4,byte[].class))
                    )
                    // 委托
                    .intercept(MethodDelegation.to(CacheInterceptor.class));

            new AgentBuilder
                    .Default()
                    // 指定需要拦截的类
                    .type(ElementMatchers.named("org.apache.skywalking.apm.dependencies.net.bytebuddy.agent.builder.AgentBuilder$Default$ExecutingTransformer"))
                    .transform(transformer)
                    .installOn(instrumentation);

            System.err.println("=== end start skywalking-byte-buddy-agent ===");
        } else {
            System.err.println("=== enable is false, not start skywalking-byte-buddy-agent ===");
        }
    }
}
