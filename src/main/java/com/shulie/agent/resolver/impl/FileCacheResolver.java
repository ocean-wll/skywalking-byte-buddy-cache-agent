package com.shulie.agent.resolver.impl;

import com.shulie.agent.resolver.Cache;
import com.shulie.agent.resolver.ClassCacheResolver;
import com.shulie.agent.util.FileUtils;
import com.shulie.agent.util.IOUtils;

import java.io.*;

/**
 * @Description 文件缓存处理器
 * @Author ocean_wll
 * @Date 2021/8/5 4:07 下午
 */
public class FileCacheResolver implements ClassCacheResolver {

    /**
     * 缓存目录
     */
    private final File cacheDir;

    public FileCacheResolver(File cacheDir) {
        this.cacheDir = cacheDir;

        // 退出时清理缓存目录
        FileUtils.deleteDirectoryOnExit(cacheDir);
    }

    @Override
    public byte[] getClassCache(ClassLoader loader, String className) {
        // load from cache
        File cacheFile = getCacheFile(loader, className);
        if (cacheFile.exists()) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(cacheFile);
                return IOUtils.toByteArray(fileInputStream);
            } catch (IOException e) {
                System.err.println("load class bytes from cache file failure");
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(fileInputStream);
            }
        }
        return null;
    }

    @Override
    public void putClassCache(ClassLoader loader, String className, byte[] classfileBuffer) {
        File cacheFile = getCacheFile(loader, className);
        cacheFile.getParentFile().mkdirs();
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(cacheFile);
            IOUtils.copy(new ByteArrayInputStream(classfileBuffer), output);
        } catch (IOException e) {
            System.err.println("save class bytes to cache file failure");
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * 获取缓存文件
     *
     * @param loader    ClassLoader
     * @param className 类名
     * @return File
     */
    private File getCacheFile(ClassLoader loader, String className) {
        String filename = Cache.getClassLoaderHash(loader) + "/" + className.replace('.', '/') + ".class";
        return new File(cacheDir, filename);
    }
}
