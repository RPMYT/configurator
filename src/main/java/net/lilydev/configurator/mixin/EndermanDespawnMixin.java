package net.lilydev.configurator.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.EndermanEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public abstract class EndermanDespawnMixin {
    @Shadow @Nullable public abstract BlockState getCarriedBlock();

    /**
     * Allows despawning unless this {@code EndermanEntity} is carrying a {@code BlockEntity}.
     * <br> This deviates from the normal behaviour of disallowing despawning if any block is being carried.
     */
    @Inject(method = "cannotDespawn", at = @At("HEAD"), cancellable = true)
    private void allowDespawning(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.getCarriedBlock() != null && this.getCarriedBlock().hasBlockEntity());
    }
}