package com.restonecash.sansystem.api.san;

/**
 * 同步跟踪器接口
 * 负责追踪 San 值变化，用于触发网络同步
 * 这是一个独立的关注点，与核心值管理分离
 */
public interface SanityTracker
{
    /**
     * 检测值是否发生变化
     * @return true 如果自上次 clearChanged() 后有变化
     */
    boolean hasChanged();

    /**
     * 清除变化标记
     * 在同步数据包发送后调用
     */
    void clearChanged();

    /**
     * 标记为已变化
     * 由内部状态变化触发
     */
    void markChanged();
}
