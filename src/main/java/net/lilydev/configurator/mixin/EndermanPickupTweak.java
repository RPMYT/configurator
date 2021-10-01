package net.lilydev.configurator.mixin;

import net.lilydev.configurator.ConfigLoader;
import net.lilydev.configurator.util.BlockDataStorage;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(EndermanEntity.PickUpBlockGoal.class)
public class EndermanPickupTweak {
    @Shadow @Final
    private EndermanEntity enderman;

    /**
     * @author PrismaticyT
     */
    @Overwrite
    public void tick() {
        Random random = this.enderman.getRandom();
        World world = this.enderman.world;
        int targetX = MathHelper.floor(this.enderman.getX() - 2.0D + random.nextDouble() * 4.0D);
        int targetY = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 3.0D);
        int targetZ = MathHelper.floor(this.enderman.getZ() - 2.0D + random.nextDouble() * 4.0D);
        BlockPos targetPos = new BlockPos(targetX, targetY, targetZ);
        BlockState targetState = world.getBlockState(targetPos);
        Vec3d vec3d = new Vec3d((double)this.enderman.getBlockX() + 0.5D, (double) targetY + 0.5D, (double)this.enderman.getBlockZ() + 0.5D);
        Vec3d vec3d2 = new Vec3d((double) targetX + 0.5D, (double) targetY + 0.5D, (double) targetZ + 0.5D);
        BlockHitResult result = world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, this.enderman));
        boolean bl = result.getBlockPos().equals(targetPos);
        if (!ConfigLoader.ENDERMAN_BLACKLIST.contains(targetState.getBlock()) && ConfigLoader.ENDERMAN_FALLBACK.get().equals("allow") || (!ConfigLoader.ENDERMAN_FALLBACK.get().equals("allow") && !ConfigLoader.ENDERMAN_FALLBACK.get().equals("deny")) && targetState.isIn(BlockTags.ENDERMAN_HOLDABLE) && bl || ConfigLoader.ENDERMAN_WHITELIST.contains(targetState.getBlock())) {
            this.enderman.setCarriedBlock(targetState);
            if (targetState.getBlock() instanceof BlockEntityProvider provider && ConfigLoader.ENDERMAN_ALLOW_BLOCKENTITIES.get()) {
                BlockEntity blockEntity = world.getBlockEntity(targetPos);
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
                    world.removeBlockEntity(targetPos);
                }
            }
            world.removeBlock(targetPos, false);
        }
    }
}
