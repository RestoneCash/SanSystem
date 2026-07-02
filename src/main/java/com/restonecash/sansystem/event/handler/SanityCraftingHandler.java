package com.restonecash.sansystem.event.handler;

import com.restonecash.sansystem.capability.SanityCapability;
import com.restonecash.sansystem.config.ServerConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 合成系统处理器（核心系统，手动注册）
 * 注册位置：SanSystem.commonSetup()
 * 功能：监听玩家Tick事件，检测合成结果物品的San值下限，不足则阻止合成
 * 规则：合成所需最低San值 = 所有材料物品的San值要求之和（每个材料按个数累加）
 *       合成失败时只清空结果槽，不消耗材料
 */
public class SanityCraftingHandler
{
    @SubscribeEvent
    public void onPlayerTick(LivingEvent.LivingTickEvent event)
    {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        for (Slot slot : player.containerMenu.slots) {
            if (!(slot instanceof ResultSlot resultSlot)) continue;
            if (!(resultSlot.container instanceof CraftingContainer craftingContainer)) continue;

            ItemStack resultStack = resultSlot.getItem();
            if (resultStack.isEmpty()) continue;

            float requiredSanity = calculateRequiredSanity(craftingContainer);
            if (requiredSanity <= 0) continue;

            player.getCapability(SanityCapability.SANITY).ifPresent(sanity -> {
                if (sanity.getCore().getSanity() < requiredSanity) {
                    resultSlot.set(ItemStack.EMPTY);
                }
            });
        }
    }

    private float calculateRequiredSanity(CraftingContainer container)
    {
        float total = 0.0f;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            Item item = stack.getItem();
            float itemRequirement = ServerConfig.getCraftingSanityRequirement(item);
            if (itemRequirement > 0) {
                total += itemRequirement * stack.getCount();
            }
        }
        return total;
    }
}