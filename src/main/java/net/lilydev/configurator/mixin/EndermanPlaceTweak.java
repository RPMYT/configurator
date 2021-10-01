package net.lilydev.configurator.mixin;

import net.lilydev.configurator.util.BlockDataStorage;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(EndermanEntity.PlaceBlockGoal.class)
public abstract class EndermanPlaceTweak {
    @Shadow
    @Final
    private EndermanEntity enderman;

    @Shadow
    protected abstract boolean canPlaceOn(World world, BlockPos posAbove, BlockState carriedState, BlockState stateAbove, BlockState state, BlockPos pos);

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;postProcessState(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void tick(CallbackInfo ci, Random random, World world, int i, int j, int k, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2, BlockState blockState3) {
        if (this.canPlaceOn(world, blockPos, blockState3, blockState, blockState2, blockPos2)) {
            world.setBlockState(blockPos, blockState3, 3);
            world.emitGameEvent(this.enderman, GameEvent.BLOCK_PLACE, blockPos);
            if (this.enderman.getCarriedBlock() != null && this.enderman.getCarriedBlock().hasBlockEntity()) {
                NbtCompound data = ((BlockDataStorage) this.enderman).readBlockData();
                BlockEntity blockEntity = ((BlockEntityProvider) this.enderman.getCarriedBlock().getBlock()).createBlockEntity(blockPos, blockState3);
                if (blockEntity != null) {
                    blockEntity.readNbt(data);
                    world.addBlockEntity(blockEntity);
                }
            }
            this.enderman.setCarriedBlock(null);
        } else {
            ci.cancel();
        }
    }
}
