package com.restonecash.sansystem.api.attribute;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class SanRangedAttribute extends RangedAttribute implements ISanAttribute
{
    /**
     * 构造函数，创建一个带数值上下限的san属性
     * @param pDescriptionId 本地化翻译键，用于游戏内属性名称、词条提示文本
     * @param pDefaultValue 实体无装备、无Buff时的基础默认倍率值（本模组统一1.0代表无增减）
     * @param pMin 属性允许的最小数值，低于该值会被强制截断到此数值
     * @param pMax 属性允许的最大数值，高于该值会被强制截断到此数值
     */
    public SanRangedAttribute(String pDescriptionId, double pDefaultValue, double pMin, double pMax)
    {
        super(pDescriptionId, pDefaultValue, pMin, pMax);
    }
}
