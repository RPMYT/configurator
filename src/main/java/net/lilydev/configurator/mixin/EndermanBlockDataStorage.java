package net.lilydev.configurator.mixin;

import net.lilydev.configurator.util.BlockDataStorage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermanEntity.class)
public abstract class EndermanBlockDataStorage extends LivingEntity implements BlockDataStorage {
    @Unique
    private NbtCompound blockData;

    protected EndermanBlockDataStorage(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void writeBlockData(NbtCompound data) {
        this.blockData = data;
    }

    @Override
    public NbtCompound readBlockData() {
        return this.blockData;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void serialize(NbtCompound nbt, CallbackInfo ci) {
        nbt.put("BlockData", this.blockData);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void deserialize(NbtCompound nbt, CallbackInfo ci) {
        this.blockData = nbt.getCompound("BlockData");
    }
}