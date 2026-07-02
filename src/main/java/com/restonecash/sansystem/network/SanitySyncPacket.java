package com.restonecash.sansystem.network;

import com.restonecash.sansystem.capability.SanityCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 理智同步数据包
 * 从服务器向客户端同步 San 值和运行时状态
 *
 * 【同步字段说明】
 * - entityId: 实体ID
 * - sanity: 当前 San 值
 * - maxSanity: 最大 San 值
 * - inputInverted: 输入是否反转
 * - slownessStacks: 减速效果叠加层数
 * - nauseaIntensity: 恶心效果强度（0-1）
 * - blindnessIntensity: 失明效果强度（0-1）
 *
 * 【宽限期说明】
 * 宽限期开始时间（gracePeriodStart）不通过此数据包同步，
 * 因为宽限期是基于服务器端 tick 计算的，客户端不需要知道具体时间，
 * 只需要知道当前效果强度即可。
 */
public class SanitySyncPacket
{
    private final int entityId;
    private final float sanity;
    private final float maxSanity;
    private final boolean inputInverted;
    private final int slownessStacks;
    private final float nauseaIntensity;
    private final float blindnessIntensity;

    /**
     * 完整构造函数
     * @param entityId 实体ID
     * @param sanity 当前 San 值
     * @param maxSanity 最大 San 值
     * @param inputInverted 输入是否反转
     * @param slownessStacks 减速效果叠加层数
     * @param nauseaIntensity 恶心效果强度（0-1）
     * @param blindnessIntensity 失明效果强度（0-1）
     */
    public SanitySyncPacket(int entityId, float sanity, float maxSanity, boolean inputInverted,
                            int slownessStacks, float nauseaIntensity, float blindnessIntensity)
    {
        this.entityId = entityId;
        this.sanity = sanity;
        this.maxSanity = maxSanity;
        this.inputInverted = inputInverted;
        this.slownessStacks = slownessStacks;
        this.nauseaIntensity = nauseaIntensity;
        this.blindnessIntensity = blindnessIntensity;
    }

    /**
     * 解码构造函数（从网络读取）
     */
    public SanitySyncPacket(FriendlyByteBuf buf)
    {
        this.entityId = buf.readInt();
        this.sanity = buf.readFloat();
        this.maxSanity = buf.readFloat();
        this.inputInverted = buf.readBoolean();
        this.slownessStacks = buf.readInt();
        this.nauseaIntensity = buf.readFloat();
        this.blindnessIntensity = buf.readFloat();
    }

    /**
     * 编码（写入网络）
     */
    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeInt(this.entityId);
        buf.writeFloat(this.sanity);
        buf.writeFloat(this.maxSanity);
        buf.writeBoolean(this.inputInverted);
        buf.writeInt(this.slownessStacks);
        buf.writeFloat(this.nauseaIntensity);
        buf.writeFloat(this.blindnessIntensity);
    }

    /**
     * 处理数据包（客户端）
     */
    public void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> {
            if (Minecraft.getInstance().level != null) {
                Entity entity = Minecraft.getInstance().level.getEntity(entityId);
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.getCapability(SanityCapability.SANITY).ifPresent(sanityCap -> {
                        sanityCap.getCore().setMaxSanity(this.maxSanity);
                        sanityCap.getCore().setSanity(this.sanity);
                        sanityCap.getEffects().setInputInverted(this.inputInverted);
                        sanityCap.getEffects().setSlownessStacks(this.slownessStacks);
                        sanityCap.getEffects().setNauseaIntensity(this.nauseaIntensity);
                        sanityCap.getEffects().setBlindnessIntensity(this.blindnessIntensity);
                        sanityCap.getSyncTracker().clearChanged();
                    });
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
