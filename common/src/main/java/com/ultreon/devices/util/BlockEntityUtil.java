package com.ultreon.devices.util;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityUtil {
    public static void markBlockForUpdate(Level level, BlockPos pos) {
        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> getTicker() {
        return (pLevel, pPos, pState, pBlockEntity) -> {
            if (pBlockEntity instanceof Tickable) {
                ((Tickable) pBlockEntity).tick();
            }
        };
    }

    public static void setBlockState(Level level, BlockPos pos, BlockState state, int flags) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.setBlock(pos, state, flags);
        }
    }


    /**
     * Send block state update.
     * Note: this code is copied from Machinizing which is licensed under the Ultreon API License. Because I'm the owner of that project I'm allowed to use it.
     *
     * @param newState the new state.
     * @author Qboi123
     */
    public static void sendUpdate(Level level, BlockPos pos, BlockState newState) {
        if (level == null) return;
        BlockState oldState = level.getBlockState(pos);
        if (oldState != newState) {
            level.setBlock(pos, newState, 0b00000011);
            level.sendBlockUpdated(pos, oldState, newState, 0b00000011);
            level.updateNeighborsAt(pos, newState.getBlock());

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.getServer().getPlayerList().getPlayers().forEach(player -> player.connection.send(new ClientboundBlockUpdatePacket(serverLevel, pos)));
            }
        }
    }

}
