package com.ultreon.devices.block;

import com.ultreon.devices.block.entity.MacMaxXBlockEntity;
import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MacMaxXBlock extends ComputerBlock {
    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(-16, 31, 5, 32, 32, 7),
            Block.box(-15, 4, 5.5, 31, 32, 7),
            Block.box(-16, 5, 5, -15, 32, 7),
            Block.box(-16, 3, 5, 32, 7, 7),
            Block.box(31, 5, 5, 32, 32, 7),
            Block.box(-6, 0.5, 6.5, -2, 19, 9),
            Block.box(20, 0.5, 6.5, 24, 19, 9),
            Block.box(0, 13, 6.5, 18, 27, 8),
            Block.box(19, 0, 6.5, 25, 0.5, 9),
            Block.box(-7, 0, 6.5, -1, 0.5, 9)
    );
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(9, 31, -16, 11, 32, 32),
            Block.box(9, 4, -15, 10.5, 32, 31),
            Block.box(9, 5, -16, 11, 32, -15),
            Block.box(9, 3, -16, 11, 7, 32),
            Block.box(9, 5, 31, 11, 32, 32),
            Block.box(7, 0.5, -6, 9.5, 19, -2),
            Block.box(7, 0.5, 20, 9.5, 19, 24),
            Block.box(8, 13, 0, 9.5, 27, 18),
            Block.box(7, 0, 19, 9.5, 0.5, 25),
            Block.box(7, 0, -7, 9.5, 0.5, -1)
    );
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(-16, 31, 9, 32, 32, 11),
            Block.box(-15, 4, 9, 31, 32, 10.5),
            Block.box(31, 5, 9, 32, 32, 11),
            Block.box(-16, 3, 9, 32, 7, 11),
            Block.box(-16, 5, 9, -15, 32, 11),
            Block.box(18, 0.5, 7, 22, 19, 9.5),
            Block.box(-8, 0.5, 7, -4, 19, 9.5),
            Block.box(-2, 13, 8, 16, 27, 9.5),
            Block.box(-9, 0, 7, -3, 0.5, 9.5),
            Block.box(17, 0, 7, 23, 0.5, 9.5)
    );
    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(5, 31, -16, 7, 32, 32),
            Block.box(5.5, 4, -15, 7, 32, 31),
            Block.box(5, 5, 31, 7, 32, 32),
            Block.box(5, 3, -16, 7, 7, 32),
            Block.box(5, 5, -16, 7, 32, -15),
            Block.box(6.5, 0.5, 18, 9, 19, 22),
            Block.box(6.5, 0.5, -8, 9, 19, -4),
            Block.box(6.5, 13, -2, 8, 27, 16),
            Block.box(6.5, 0, -9, 9, 0.5, -3),
            Block.box(6.5, 0, 17, 9, 0.5, 23)
    );

    public MacMaxXBlock() {
        super(BlockBehaviour.Properties.of(Material.HEAVY_METAL, DyeColor.WHITE).strength(6f).sound(SoundType.METAL).noOcclusion().dynamicShape());
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
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0f;
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
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MacMaxXBlockEntity(pos, state);
    }
}
