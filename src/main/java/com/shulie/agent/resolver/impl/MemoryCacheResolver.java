package com.shulie.agent.resolver.impl;

import com.shulie.agent.resolver.Cache;
import com.shulie.agent.resolver.ClassCacheResolver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 内存缓存解析器
 * @Author ocean_wll
 * @Date 2021/8/5 4:03 下午
 */
public class MemoryCacheResolver implements ClassCacheResolver {

    /**
     * key为 classloader+className，value为 字节码
     */
    private final Map<String, byte[]> classCacheMap = new ConcurrentHashMap<>();

    @Override
    public byte[] getClassCache(ClassLoader loader, String className) {
        String cacheKey = getCacheKey(loader, className);
        return classCacheMap.get(cacheKey);
    }

    @Override
    public void putClassCache(ClassLoader loader, String className, byte[] classfileBuffer) {
        String cacheKey = getCacheKey(loader, className);
        classCacheMap.put(cacheKey, classfileBuffer);
    }


    /**
     * 获取缓存key ClassLoaderHash(loader) + "@" + className
     *
     * @param loader    ClassLoader
     * @param className 类名
     * @return 缓存key
     */
    private String getCacheKey(ClassLoader loader, String className) {
        return Cache.getClassLoaderHash(loader) + "@" + className;
    }
}
