package com.restonecash.sansystem.api.san;

public interface ISanManager
{
    /**
     * 为指定玩家施加对应法术的冷却计时
     * 冷却逻辑仅服务端生效，客户端无独立计算能力，入参强制限定 ServerPlayer
     * @param serverPlayer 目标服务端玩家实例
     * @param spell 待施加冷却的法术对象，内含法术等级、基础冷却时长配置
     * @param castSource 施法来源枚举，区分法杖/法术书/卷轴/饰品等不同释放渠道，可根据来源倍率修改实际冷却时长
     */
    //void addCooldown(ServerPlayer serverPlayer, AbstractSpell spell, CastSource castSource);
}
