package com.shulie.agent.util;

import java.io.File;
import java.nio.file.Files;

/**
 * @Description 文件工具类
 * @Author ocean_wll
 * @Date 2021/8/5 4:35 下午
 */
public class FileUtils {

    /**
     * 递归删除目录和文件
     *
     * @param dir 删除的目录
     */
    public static void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!Files.isSymbolicLink(file.toPath())) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }

    /**
     * jvm中增加一个关闭的钩子，当jvm关闭的时候，会执行系统中已经设置的所有通过方法addShutdownHook添加的钩子，当系统执行完这些钩子后，jvm才会关闭。所以这些钩子可以在jvm关闭的时候进行内存清理、对象销毁等操作。
     *
     * @param dir 删除的文件
     */
    public static void deleteDirectoryOnExit(File dir) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> FileUtils.deleteDirectory(dir)));
    }
}
