package net.lilydev.configurator.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.tag.BlockTags;

public class EndermanPickupValidator {

    /**
     * Checks if a block can be picked up, as per the config settings.
     * <br>
     * <br>
     * Checks are as follows: <br>
     *   - Is the block present in the blacklist? (invalid) <br>
     *   - Is the block not present in the whitelist and the default behaviour is to deny? (invalid) <br>
     *   - Is the block present in the whitelist? (valid) <br>
     *   - Is the block not present in the blacklist and the default behaviour is to allow? (valid) <br>
     *   - Is the block a BlockEntity and the config disallows picking those up? (invalid) <br>
     *   - Is the block unbreakable and the config disallows picking up unbreakable blocks? (invalid) <br>
     *   - Is the default behaviour 'allow'? (valid) <br>
     *   - Is the default behaviour 'deny'? (invalid) <br>
     *   - Is the block air, or is it invisible? (invalid) <br>
     *   - Finally, as a last resort fallback, can block be picked up as per vanilla logic?
     * <br>
     * <br>
     * @param block The block to check for pickup validity.
     * @return Whether or not the block can be picked up.
     */
    public static boolean isValid(Block block) {
        if (ConfigLoader.ENDERMAN_PICKUP_BLACKLIST.contains(block)) {
            return false;
        }

        if (!ConfigLoader.ENDERMAN_PICKUP_WHITELIST.contains(block) && ConfigLoader.ENDERMAN_FALLBACK_BEHAVIOUR.get().equals("deny")) {
            return false;
        }

        if (ConfigLoader.ENDERMAN_PICKUP_WHITELIST.contains(block)) {
            return true;
        }

        if (!ConfigLoader.ENDERMAN_PICKUP_BLACKLIST.contains(block) && ConfigLoader.ENDERMAN_FALLBACK_BEHAVIOUR.get().equals("allow")) {
            return true;
        }

        if (!ConfigLoader.ENDERMAN_ALLOW_BLOCKENTITIES.get() && block instanceof BlockEntityProvider) {
            return false;
        }

        if (!ConfigLoader.ENDERMAN_ALLOW_UNBREAKABLES.get() && block.getHardness() == 3600000.0F) {
            return false;
        }

        if (ConfigLoader.ENDERMAN_FALLBACK_BEHAVIOUR.get().equals("allow")) {
            return true;
        }

        if (ConfigLoader.ENDERMAN_FALLBACK_BEHAVIOUR.get().equals("deny")) {
            return false;
        }

        if (block.getDefaultState().isAir() || block.getDefaultState().getRenderType() == BlockRenderType.INVISIBLE) {
            return false;
        }

        return BlockTags.ENDERMAN_HOLDABLE.contains(block);
    }
}
