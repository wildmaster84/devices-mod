package com.ultreon.devices.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public class BlockEntityUtil {
    public static void markBlockForUpdate(Level level, BlockPos pos) {
        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> getTicker() {
        return (pLevel, pPos, pState, pBlockEntity) -> {
            if (pBlockEntity instanceof ITickable) {
                ((ITickable) pBlockEntity).tick();
            }
        };
    }
}
