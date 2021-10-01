package net.lilydev.configurator.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;

public class EndermanPickupValidator {
    public static boolean isValid(Block block) {
        if (ConfigLoader.ENDERMAN_BLACKLIST.contains(block)) {
            return false;
        }

        if (!ConfigLoader.ENDERMAN_WHITELIST.contains(block) && ConfigLoader.ENDERMAN_FALLBACK.get().equals("deny")) {
            return false;
        }

        if (ConfigLoader.ENDERMAN_WHITELIST.contains(block)) {
            return true;
        }

        if (!ConfigLoader.ENDERMAN_BLACKLIST.contains(block) && ConfigLoader.ENDERMAN_FALLBACK.get().equals("allow")) {
            return true;
        }

        if (!ConfigLoader.ENDERMAN_ALLOW_BLOCKENTITIES.get() && block instanceof BlockEntityProvider) {
            return false;
        }

        if (!ConfigLoader.ENDERMAN_ALLOW_UNBREAKABLES.get() && block.getHardness() == 3600000.0F) {
            return false;
        }

        if (ConfigLoader.ENDERMAN_FALLBACK.get().equals("allow")) {
            return true;
        }

        if (ConfigLoader.ENDERMAN_FALLBACK.get().equals("deny")) {
            return false;
        }

        if (block.getDefaultState().isAir() || block.getDefaultState().getRenderType() == BlockRenderType.INVISIBLE) {
            return false;
        }

        return BlockTags.ENDERMAN_HOLDABLE.contains(block);
    }
}
