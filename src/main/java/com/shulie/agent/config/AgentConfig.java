package com.shulie.agent.config;

import com.shulie.agent.constant.CacheMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @Description agent启动配置类
 * @Author ocean_wll
 * @Date 2021/8/5 2:58 下午
 */
public class AgentConfig {

    //======================== 这些熟悉属于全局参数 =============================
    /**
     * 默认是开启缓存
     */
    public static boolean enable = true;

    /**
     * 缓存数据存储类型，默认是内存
     */
    public static CacheMode cacheMode = CacheMode.MEMORY;

    //======================== 下面的属性属于AgentConfig本身 =============================
    /**
     * 单例
     */
    private volatile static AgentConfig agentConfig;

    /**
     * 启动参数默认前缀
     */
    private final static String PREFIX = "byteBuddyCache.";

    /**
     * keyProcessor集合
     */
    private final List<KeyProcessor> PROCESSOR_LIST;

    private AgentConfig() {
        PROCESSOR_LIST = Arrays.asList(
                new EnableProcessor(),
                new CacheModeProcessor()
        );
    }

    public static AgentConfig instance() {
        if (agentConfig == null) {
            synchronized (AgentConfig.class) {
                if (agentConfig == null) {
                    agentConfig = new AgentConfig();
                }
            }
        }
        return agentConfig;
    }


    /**
     * 预处理启动参数
     */
    public void initConfig() {
        PROCESSOR_LIST.forEach(processor -> {
            String value = System.getProperty(PREFIX + processor.getKey());
            if (value == null || "".equals(value)) {
                return;
            }
            processor.dealValue(value);
        });
    }

    /**
     * jvm启动key处理接口
     */
    interface KeyProcessor {

        /**
         * 获取key
         *
         * @return key
         */
        String getKey();

        /**
         * 处理value
         *
         * @param value 启动的值
         */
        void dealValue(String value);
    }

    /**
     * 处理enable
     */
    final class EnableProcessor implements KeyProcessor {

        @Override
        public String getKey() {
            return "enable";
        }

        @Override
        public void dealValue(String value) {
            try {
                AgentConfig.enable = Boolean.parseBoolean(value);
            } catch (Exception e) {
                System.err.println("=== illegal value, key:" + PREFIX + getKey() + " value: " + value + " ===");
            }
        }
    }

    /**
     * 处理cacheMode
     */
    final class CacheModeProcessor implements KeyProcessor {

        @Override
        public String getKey() {
            return "cacheMode";
        }

        @Override
        public void dealValue(String value) {
            try {
                AgentConfig.cacheMode = CacheMode.valueOf(value.toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                System.err.println("=== illegal value, key:" + PREFIX + getKey() + " value: " + value + " ===");
            }
        }
    }
}
