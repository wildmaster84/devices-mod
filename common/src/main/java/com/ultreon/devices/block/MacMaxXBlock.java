package com.ultreon.devices.block;

import com.ultreon.devices.block.entity.MacMaxXBlockEntity;
import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MacMaxXBlock extends ComputerBlock {
    private static final VoxelShape SHAPE_NORTH = Block.box(-16, 0, 7, 32, 35.25, 11);
    private static final VoxelShape SHAPE_EAST = Block.box(-16, 0, 7, 32, 35.25, 11);
    private static final VoxelShape SHAPE_SOUTH = Block.box(-16, 0, 7, 32, 35.25, 11);
    private static final VoxelShape SHAPE_WEST = Block.box(-16, 0, 7, 32, 35.25, 11);

    public MacMaxXBlock() {
        super(BlockBehaviour.Properties.of(Material.HEAVY_METAL, DyeColor.WHITE).strength(6f).sound(SoundType.METAL));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        };
    }

    @Override
    public boolean isDesktopPC() {
        return true;
    }

    @Override
    public MutableComponent getName() {
        MutableComponent normalName = Component.translatable("block.devices.mac_max_x");
        if (Platform.isModLoaded("emojiful")) {
            return Component.translatable("block.devices.mac_max_x_emoji");
        }
        return normalName;
    }

    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MacMaxXBlockEntity(pos, state);
    }
}
