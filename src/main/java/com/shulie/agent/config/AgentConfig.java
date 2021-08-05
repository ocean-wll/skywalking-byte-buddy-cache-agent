package com.shulie.agent.config;

import com.shulie.agent.constant.CacheMode;

/**
 * @Description agent启动配置类
 * @Author ocean_wll
 * @Date 2021/8/5 2:58 下午
 */
public class AgentConfig {

    /**
     * 默认是开启缓存
     */
    public static boolean enable = true;

    /**
     * 缓存数据存储类型，默认是内存
     */
    public static CacheMode cacheMode = CacheMode.MEMORY;

}
