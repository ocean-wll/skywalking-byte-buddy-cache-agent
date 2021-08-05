package com.shulie.agent.resolver;

/**
 * @Description cacheResolver接口
 * @Author ocean_wll
 * @Date 2021/8/5 4:02 下午
 */
public interface ClassCacheResolver {

    /**
     * 获取class缓存
     *
     * @param loader    ClassLoader
     * @param className 类名
     * @return byte数组
     */
    byte[] getClassCache(ClassLoader loader, String className);

    /**
     * 存放class缓存
     *
     * @param loader          ClassLoader
     * @param className       类名
     * @param classfileBuffer 字节码数据
     */
    void putClassCache(ClassLoader loader, String className, byte[] classfileBuffer);
}
