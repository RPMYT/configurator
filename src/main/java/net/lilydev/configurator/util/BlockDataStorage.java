package net.lilydev.configurator.util;

import net.minecraft.nbt.NbtCompound;

public interface BlockDataStorage {
    void writeBlockData(NbtCompound data);
    NbtCompound readBlockData();
}
