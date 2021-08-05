package com.shulie.agent.resolver;

import com.shulie.agent.boot.AgentPackagePath;
import com.shulie.agent.config.AgentConfig;
import com.shulie.agent.constant.CacheMode;
import com.shulie.agent.resolver.impl.FileCacheResolver;
import com.shulie.agent.resolver.impl.MemoryCacheResolver;
import net.bytebuddy.utility.RandomString;

import java.io.File;

/**
 * @Description 缓存解析器
 * @Author ocean_wll
 * @Date 2021/8/5 11:48 上午
 */
public class Cache {

    /**
     * 缓存解析器
     */
    private static ClassCacheResolver classCacheResolver;

    static {
        if (CacheMode.FILE.equals(AgentConfig.cacheMode)) {
            String cacheDirBase = null;
            try {
                cacheDirBase = AgentPackagePath.getPath() + "/class-cache";
            } catch (RuntimeException e) {
                e.printStackTrace();
                throw new RuntimeException("Can't find the root path for creating /class-cache folder.");
            }
            File cacheDir = new File(cacheDirBase + "/class-cache-" + RandomString.make());
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            if (!cacheDir.exists()) {
                throw new RuntimeException("Create class cache dir failure");
            }
            System.err.println("=== fileCacheDir: " + cacheDir.getPath());
            classCacheResolver = new FileCacheResolver(cacheDir);
        } else {
            classCacheResolver = new MemoryCacheResolver();
        }
    }

    /**
     * 获取class缓存
     *
     * @param loader    ClassLoader
     * @param className className
     * @return byte数组
     */
    public static byte[] getClassCache(ClassLoader loader, String className) {
        return classCacheResolver.getClassCache(loader, className);
    }

    /**
     * 存放class缓存
     *
     * @param loader          ClassLoader
     * @param className       className
     * @param classfileBuffer class字节码
     */
    public static void putClassCache(ClassLoader loader, String className, byte[] classfileBuffer) {
        classCacheResolver.putClassCache(loader, className, classfileBuffer);
    }


    /**
     * 获取classLoader的hash值
     *
     * @param loader ClassLoader
     * @return classLoad的hash值
     */
    public static String getClassLoaderHash(ClassLoader loader) {
        String classloader;
        if (loader != null) {
            classloader = Integer.toHexString(loader.hashCode());
        } else {
            //classloader is null for BootstrapClassLoader
            classloader = "00000000";
        }
        return classloader;
    }
}
