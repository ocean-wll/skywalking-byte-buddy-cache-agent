package com.shulie.agent;

import com.shulie.agent.config.AgentConfig;
import com.shulie.agent.constant.CacheMode;
import com.shulie.agent.interceptor.CacheInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.util.Locale;

/**
 * @Description agent入口类
 * @Author ocean_wll
 * @Date 2021/8/4 1:53 下午
 */
public class Agent {

    public static void premain(final String agentArgs, final Instrumentation instrumentation) {
        System.err.println("====== skywalking-byte-buddy-agent ======");
        System.out.println("=== agentArgs：" + agentArgs + " ===");

        // 预处理启动参数
        dealAgentArgs(agentArgs);

        if (AgentConfig.enable) {
            System.err.println("=== begin start skywalking-byte-buddy-agent ===");
            System.out.println("=== cacheMode is " + AgentConfig.cacheMode + " ===");
            AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule) -> builder
                    // 拦截transform方法
                    .method(ElementMatchers.hasMethodName("transform"))
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

    /**
     * 处理agent启动参数
     *
     * @param agentArgs agent启动参数，kv组合，多个参数以;分割 示例：enable=true;cacheMode=memory
     */
    private static void dealAgentArgs(String agentArgs) {
        if (agentArgs == null || "".equals(agentArgs)) {
            return;
        }
        String[] argArray = agentArgs.split(";");
        for (String arg : argArray) {
            try {
                String[] argItem = arg.split("=");
                switch (argItem[0]) {
                    case "enable":
                        AgentConfig.enable = Boolean.parseBoolean(argItem[1]);
                        break;
                    case "cacheMode":
                        AgentConfig.cacheMode = CacheMode.valueOf(argItem[1].toUpperCase(Locale.ROOT));
                        break;
                }
            } catch (Exception e) {
                System.err.println("=== illegal arg: " + arg + " ===");
            }
        }
    }


}
