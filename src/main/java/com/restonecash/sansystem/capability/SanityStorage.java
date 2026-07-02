//NBT存储序列化

package com.restonecash.sansystem.capability;

import com.restonecash.sansystem.api.san.ISanity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


// 实现序列化接口，泛型指定为 CompoundTag
public class SanityStorage implements ICapabilitySerializable<CompoundTag>
{
    // 你的理智能力实例
    private final ISanity sanityInstance = new SanityCapability();
    // 懒加载可选包装，Forge标准写法
    private final LazyOptional<ISanity> lazyOptional = LazyOptional.of(() -> sanityInstance);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // 匹配到我们注册的理智能力时返回
        if (cap == SanityCapability.SANITY) {
            return lazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();
        sanityInstance.saveNBT(tag);
        return tag;
    }

    // 读档加载：等价于原来 IStorage 的 readNBT
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        sanityInstance.loadNBT(nbt);
    }
}
