package net.lilydev.configurator.mixin;

import net.lilydev.configurator.util.BlockDataStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(EndermanEntity.PlaceBlockGoal.class)
public abstract class EndermanPlaceTweak {
    @Shadow
    @Final
    private EndermanEntity enderman;

    @Shadow
    protected abstract boolean canPlaceOn(World world, BlockPos posAbove, BlockState carriedState, BlockState stateAbove, BlockState state, BlockPos pos);

    /**
     * @author PrismaticYT
     */
    @Overwrite
    public void tick() {
        Random random = this.enderman.getRandom();
        World world = this.enderman.world;
        int targetX = MathHelper.floor(this.enderman.getX() - 1.0D + random.nextDouble() * 2.0D);
        int targetY = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 2.0D);
        int targetZ = MathHelper.floor(this.enderman.getZ() - 1.0D + random.nextDouble() * 2.0D);
        BlockPos targetPos = new BlockPos(targetX, targetY, targetZ);
        BlockState targetState = world.getBlockState(targetPos);
        BlockPos underTargetPos = targetPos.down();
        BlockState underTargetState = world.getBlockState(underTargetPos);
        BlockState carriedState = this.enderman.getCarriedBlock();
        if (carriedState != null) {
            carriedState = Block.postProcessState(carriedState, this.enderman.world, targetPos);
            if (this.canPlaceOn(world, targetPos, carriedState, targetState, underTargetState, underTargetPos)) {
                world.setBlockState(targetPos, carriedState, 3);
                world.emitGameEvent(this.enderman, GameEvent.BLOCK_PLACE, targetPos);
                if (this.enderman.getCarriedBlock().hasBlockEntity()) {
                    NbtCompound data = ((BlockDataStorage) this.enderman).readBlockData();
                    BlockEntity blockEntity = ((BlockEntityProvider) this.enderman.getCarriedBlock().getBlock()).createBlockEntity(targetPos, carriedState);
                    if (blockEntity != null) {
                        blockEntity.readNbt(data);
                        world.addBlockEntity(blockEntity);
                    }
                }
                this.enderman.setCarriedBlock(null);
            }
        }
    }
}
