package com.shulie.agent.interceptor;

import com.shulie.agent.resolver.Cache;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @Description 缓存拦截器
 * @Author ocean_wll
 * @Date 2021/8/5 11:53 上午
 */
public class CacheInterceptor {

    @RuntimeType
    public static Object intercept(@Origin Method method, @AllArguments Object[] args,
                                   @SuperCall Callable<?> callable) {
        Object returnObj = null;

        try {
            // 校验参数
            if (checkArgs(args)) {
                ClassLoader classLoader = (ClassLoader) args[0];
                String className = (String) args[1];

                // 获取缓存中的value
                byte[] bytes = Cache.getClassCache(classLoader, className);
                if (bytes != null) {
                    return bytes;
                }
                // 调用原有方法
                returnObj = callable.call();
                if (returnObj != null) {
                    // 如果缓存中没有，并且原方法执行结果不为null，则放入缓存中
                    Cache.putClassCache(classLoader, className, (byte[]) returnObj);
                }
            } else {
                // 会出现classloader为null的情况，但还是需要去执行transform
                returnObj = callable.call();
            }
            return returnObj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnObj;
    }

    /**
     * 因为拦截的方法是五个参数，jvm中类的唯一性是根据classloader和className来确定的，所以进行增强前对方法参数进行一次校验避免方法增强错误
     * <p>
     * 需要增强的方法
     * public byte[] transform(ClassLoader classLoader,
     * String internalTypeName,
     * Class<?> classBeingRedefined,
     * ProtectionDomain protectionDomain,
     * byte[] binaryRepresentation) {
     * if (circularityLock.acquire()) {
     * try {
     * return AccessController.doPrivileged(new AgentBuilder.Default.ExecutingTransformer.LegacyVmDispatcher(classLoader,
     * internalTypeName,
     * classBeingRedefined,
     * protectionDomain,
     * binaryRepresentation), accessControlContext);
     * } finally {
     * circularityLock.release();
     * }
     * } else {
     * return NO_TRANSFORMATION;
     * }
     * }
     *
     * @param args 方法入参
     * @return true校验通过，false校验失败
     */
    private static boolean checkArgs(Object[] args) {
        // 先校验参数个数
        if (args.length == 5) {
            // 校验第一个参数，第一个参数类型是classLoader
            boolean arg0IsTrue = args[0] != null && args[0] instanceof ClassLoader;
            // 校验第二个参数，第二个参数表示的是类名，类型为String
            boolean agr1IsTrue = args[1] != null && args[1] instanceof String;
            return arg0IsTrue && agr1IsTrue;
        }
        return false;

    }
}
