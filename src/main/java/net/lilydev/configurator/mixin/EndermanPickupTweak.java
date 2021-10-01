package net.lilydev.configurator.mixin;

import net.lilydev.configurator.util.ConfigLoader;
import net.lilydev.configurator.util.BlockDataStorage;
import net.lilydev.configurator.util.EndermanPickupValidator;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(EndermanEntity.PickUpBlockGoal.class)
public class EndermanPickupTweak {
    @Shadow @Final
    private EndermanEntity enderman;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/tag/Tag;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void tick(CallbackInfo ci, Random random, World world, int i, int j, int k, BlockPos blockPos, BlockState blockState, Vec3d vec3d, Vec3d vec3d2, BlockHitResult blockHitResult, boolean bl) {
        if (EndermanPickupValidator.isValid(blockState.getBlock())) {
            if (blockState.getBlock() instanceof BlockEntityProvider && ConfigLoader.ENDERMAN_ALLOW_BLOCKENTITIES.get()) {
                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                if (blockEntity != null) {
                    NbtCompound data = blockEntity.writeNbt(new NbtCompound());
                    if (blockEntity instanceof Inventory inventory) {
                        NbtList items = (NbtList) data.get("Items");
                        if (items != null && items.isEmpty()) {
                            DefaultedList<ItemStack> stacks = DefaultedList.ofSize(inventory.size());
                            for (int slot = 0; slot < inventory.size(); slot++) {
                                ItemStack stack = inventory.getStack(slot);
                                stacks.add(stack);
                                inventory.removeStack(slot);
                            }
                            Inventories.writeNbt(data, stacks);
                        }
                    }
                    ((BlockDataStorage) this.enderman).writeBlockData(data);
                    world.removeBlockEntity(blockPos);
                }
            }
            world.removeBlock(blockPos, false);
        } else {
            ci.cancel();
        }
    }
}
